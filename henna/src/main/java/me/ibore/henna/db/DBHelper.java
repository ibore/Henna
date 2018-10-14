package me.ibore.henna.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DBHelper {

    private Context mContext;
    private DBHelperProxy mProxy;
    private List<Table<?>> mTables = new ArrayList<Table<?>>();
    private static Map<String, DBHelper> tableMap = new HashMap<String, DBHelper>();
    private static Map<String, DBHelperProxy> dbMap = new HashMap<String, DBHelperProxy>();

    public DBHelper(Context context) {
        mContext = context;
    }

    public final void open() {
        String dbName = getDataBaseName();
        int version = getDataBaseVersion();
        if (version <= 0) {
            throw new RuntimeException("getDataBaseVersion() must > 0 !!");
        }
        synchronized (dbMap) {
            if (dbMap.containsKey(dbName)) {
                //LogUtils.w(dbName + " is already open");
                return;
            }
        }
        List<Table<?>> temp = getTables();
        if (temp == null || temp.size() == 0) {
            throw new RuntimeException("getTables() can not null !!");
        }
        mTables.addAll(temp);
        mProxy = new DBHelperProxy(mContext, dbName, null, version);
        synchronized (dbMap) {
            if (!dbMap.containsKey(dbName)) {
                dbMap.put(dbName, mProxy);
            }
        }
        for (Table<?> table : mTables) {
            table.setDBHelper(this);
            synchronized (tableMap) {
                if (!tableMap.containsKey(table.getTableName())) {
                    tableMap.put(table.getTableName(), this);
                }
            }
        }
    }

    protected static DBHelper getDBHelper(String tableName) {
        synchronized (tableMap) {
            return tableMap.get(tableName);
        }
    }

    public final void close() {
        if (mProxy != null) {
            mProxy.close();
        }
    }

    protected DBHelperProxy getDBProxy() {
        return mProxy;
    }

    /**
     * 数据库名称
     *
     * @return
     */
    public abstract String getDataBaseName();

    /**
     * 数据库版本号
     *
     * @return
     */
    public abstract int getDataBaseVersion();

    /**
     * 返回数据库下所有的表
     *
     * @return
     */
    public abstract List<Table<?>> getTables();

    class DBHelperProxy extends SQLiteOpenHelper {

        public DBHelperProxy(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (Table<?> table : mTables) {
                createTable(db, table);
            }
        }

        private void createTable(SQLiteDatabase db, Table<?> table) {
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE IF NOT EXISTS ");
            String tableName = table.getTableName();
            sql.append(tableName).append(" (");
            Class<?> tableCls = null;
            Type t = table.getClass().getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                Type[] type = ((ParameterizedType) t).getActualTypeArguments();
                tableCls = (Class<?>) type[0];
            }
            Field[] fields = tableCls.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    Column ano = field.getAnnotation(Column.class);
                    String fieldName = ano.name();
                    sql.append(fieldName).append(" ");
                    sql.append(getFieldType(ano, field)).append(" ");
                    if (ano.primaryKey()) {
                        sql.append("PRIMARY KEY").append(" ");
                    }
                    if (ano.autoIncrement()) {
                        sql.append("AUTOINCREMENT").append(" ");
                    }
                    if (ano.unique()) {
                        sql.append("UNIQUE").append(" ");
                    }
                    if (ano.notNull()) {
                        sql.append("NOT NULL").append(" ");
                    }
                    if (!TextUtils.isEmpty(ano.defaultVal())) {
                        String fieldType = getFieldType(ano, field);
                        if ("TEXT".equals(fieldType)) {
                            sql.append("default").append(" ").append("'").append(ano.defaultVal()).append("'")
                                    .append(" ");
                        } else {
                            sql.append("default").append(" ").append(ano.defaultVal()).append(" ");
                        }
                    }
                    sql.append(", ");
                }
            }
            sql.deleteCharAt(sql.length() - 2);
            sql.append(")");
            //LogUtils.v("---------------------LightSQLite--------------------------");
            //LogUtils.v(sql.toString());
            //LogUtils.v("----------------------------------------------------------");
            try {
                db.execSQL(sql.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    Column an = field.getAnnotation(Column.class);
                    if (an.index()) {
                        String sqlStr = "CREATE INDEX IF NOT EXISTS " + table.getTableName() + "_" + an.name()
                                + "_index ON " + table.getTableName() + "(" + an.name() + ")";
                        //LogUtils.v("---------------------LightSQLite--------------------------");
                        //LogUtils.v(sqlStr);
                        //LogUtils.v("----------------------------------------------------------");
                        try {
                            db.execSQL(sqlStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            table.onCreateTrigger(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            List<String> tempTables = new ArrayList<String>();
            Cursor cur = db.rawQuery("select name from sqlite_master where type='table'", null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                for (int i = 0; i < cur.getCount(); i++) {
                    tempTables.add(cur.getString(0));
                    cur.moveToNext();
                }
            }
            cur.close();

            for (Table<?> table : mTables) {
                if (!tempTables.contains(table.getTableName())) {
                    createTable(db, table);
                } else {
                    table.onUpgrade(db, oldVersion, newVersion);
                    List<String> columns = new ArrayList<String>();
                    cur = db.rawQuery("PRAGMA table_info('" + table.getTableName() + "')", null);
                    if (cur.getCount() > 0) {
                        cur.moveToFirst();
                        for (int i = 0; i < cur.getCount(); i++) {
                            String fieldN = cur.getString(cur.getColumnIndex("name"));
                            columns.add(fieldN);
                            cur.moveToNext();
                        }
                    }
                    cur.close();
                    Class<?> tableCls = null;
                    Type t = table.getClass().getGenericSuperclass();
                    if (t != null && t instanceof ParameterizedType) {
                        Type[] type = ((ParameterizedType) t).getActualTypeArguments();
                        tableCls = (Class<?>) type[0];
                        Field[] fields = tableCls.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (field.isAnnotationPresent(Column.class)) {
                                Column an = field.getAnnotation(Column.class);
                                if (!columns.contains(an.name())) {
                                    StringBuilder addSql = new StringBuilder();
                                    addSql.append("ALTER TABLE ").append(table.getTableName()).append(" ADD ");
                                    String fieldName = an.name();
                                    addSql.append(fieldName).append(" ");
                                    String fieldType = getFieldType(an, field);
                                    addSql.append(fieldType).append(" ");
                                    if (an.primaryKey()) {
                                        addSql.append("PRIMARY KEY").append(" ");
                                    }
                                    if (an.autoIncrement()) {
                                        addSql.append("AUTOINCREMENT").append(" ");
                                    }
                                    if (an.unique()) {
                                        addSql.append("UNIQUE").append(" ");
                                    }
                                    if (an.notNull()) {
                                        addSql.append("NOT NULL").append(" ");
                                    }
                                    if (!TextUtils.isEmpty(an.defaultVal())) {
                                        if ("TEXT".equals(fieldType)) {
                                            addSql.append("default").append(" ").append("'").append(an.defaultVal())
                                                    .append("'").append(" ");
                                        } else {
                                            addSql.append("default").append(" ").append(an.defaultVal()).append(" ");
                                        }

                                    }
                                    //LogUtils.v("---------------------LightSQLite--------------------------");
                                    //LogUtils.v(addSql.toString());
                                    //LogUtils.v("----------------------------------------------------------");
                                    try {
                                        db.execSQL(addSql.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        private String getFieldType(Column column, Field field) {
            if (!"UNKNOW".equalsIgnoreCase(column.type())) {
                return column.type();
            }
            Class<?> c = field.getType();
            if (c == String.class || c == char.class) {
                return "TEXT";
            } else if (c == int.class || c == long.class || c == byte.class || c == short.class || c == boolean.class) {
                return "INTEGER";
            } else if (c == float.class || c == double.class) {
                return "REAL";
            } else if (c == byte[].class) {
                return "BLOB";
            } else {
                throw new RuntimeException(field.getName() + " 非基本数据类型");
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }
}
