package me.ibore.henna.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.ibore.henna.BuildConfig;

public final class LightSQLite<T> {

    private String mDbTableName;
    private Class<T> mEntity;
    private SQLiteDatabase mSQLiteDatabase;
    private String primaryKey;
    //主要是为了缓存数据库的列名和对象的字段映射关系
    private HashMap<String, Field> mColumnCacheMap = new HashMap<>();

    private LightSQLite(String dbName, Class<T> entity) {
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbName, null);
        this.mEntity = entity;
        autoCreateTable();
        initColumnCacheMap();
    }

    private void autoCreateTable() {
        Table table = mEntity.getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("The entity class must have a table name");
        }
        mDbTableName = table.value();
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(mDbTableName + "(");
        Field[] declaredFields = mEntity.getDeclaredFields();
        for (Field field : declaredFields) {
            //拼接数据库主键
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                sb.append(id.columns() + " INTEGER PRIMARY KEY");
                primaryKey = id.columns();
                if (id.autoincrement()) {
                    sb.append(" AUTOINCREMENT ,");
                }
            }
            //拼接数据库字段
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnsName = column.columns();

                Class<?> type = field.getType();
                String columnsType = getColumnsType(type);
                if (columnsType == null) {
                    //不支持的数据库类型
                    continue;
                }
                sb.append(columnsName + " " + columnsType);
                boolean isNull = column.isNull();
                if (!isNull) {
                    sb.append("  NOT NULL");
                }
                sb.append(",");
            }
        }
        if (sb.toString().charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(" )");

        mSQLiteDatabase.execSQL(sb.toString());

        // 更新数据库的操作，只能增加字段，不可以修改或者删除字段
        String sql = "select * from " + mDbTableName + " limit 1,0";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        for (Field declaredField : declaredFields) {
            Column column = declaredField.getAnnotation(Column.class);
            boolean isNoField = true;
            for (String columnName : columnNames) {
                if ((column != null && column.columns().equals(columnName))) {
                    isNoField = false;
                    break;
                }
            }
            if (isNoField && column != null) {
                String columnsName = column.columns();
                Class<?> type = declaredField.getType();
                String columnsType = getColumnsType(type);
                if (columnsType == null) {
                    //不支持的数据库类型
                    continue;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(columnsName + " " + columnsType);
                boolean isNull = column.isNull();
                if (!isNull) {
                    stringBuilder.append("  NOT NULL");
                }
                mSQLiteDatabase.execSQL("Alter table " + mDbTableName + " add column " + stringBuilder.toString());
            }
        }
    }

    private void initColumnCacheMap() {
        //必须根据实际数据库中的列进行映射，反射数据库实际失败，对象的注解和数据库的列不一致的问题
        String sql = "select * from " + mDbTableName + " limit 1,0";
        Cursor cursor = null;
        try {
            cursor = mSQLiteDatabase.rawQuery(sql, null);
            String[] columnNames = cursor.getColumnNames();
            Field[] declaredFields = mEntity.getDeclaredFields();
            for (String columnName : columnNames) {
                Field columnField = null;
                for (Field declaredField : declaredFields) {
                    Column column = declaredField.getAnnotation(Column.class);
                    Id id = declaredField.getAnnotation(Id.class);
                    if ((column != null && column.columns().equals(columnName))
                            || (id != null && id.columns().equals(columnName))) {
                        columnField = declaredField;
                        break;
                    }
                }
                if (columnField != null) {
                    mColumnCacheMap.put(columnName, columnField);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public long insert(T entity) {
        try {
            ContentValues contentValues = getContentValues(entity);
            return mSQLiteDatabase.insert(mDbTableName, null, contentValues);
        } catch (IllegalAccessException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return -1;
        }
    }

    public long delete(T t) {
        try {
            Object primaryKeyValue = getPrimaryKeyValue(t);
            String whereClause = primaryKey + "=?";
            String[] whereArgs = new String[]{String.valueOf((long) primaryKeyValue)};
            return delete(whereClause, whereArgs);
        } catch (IllegalAccessException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return -1;
        }
    }

    public long delete(String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.delete(mDbTableName, whereClause, whereArgs);
    }

    public long update(T t) {
        try {
            Object primaryKeyValue = getPrimaryKeyValue(t);
            //为了保证更新必须含有主键
            ContentValues values = getContentValues(t);
            String whereClause = primaryKey + "=?";
            String[] whereArgs = new String[]{String.valueOf((long) primaryKeyValue)};
            return update(values, whereClause, whereArgs);
        } catch (IllegalAccessException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return -1;
        }
    }

    public long update(ContentValues values, String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.update(mDbTableName, values, whereClause, whereArgs);
    }

    public T queryById(Long id) {
        String selection = primaryKey + "= ?";
        String[] selectionArgs = new String[]{id.toString()};
        List<T> list = query(selection, selectionArgs, null, null, null, null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<T> queryAll() {
        return query(null, null, null, null, null, null);
    }

    public List<T> query(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<T> list = new ArrayList<>();
        try {
            String[] column = mColumnCacheMap.keySet().toArray(new String[0]);
            Cursor cursor = mSQLiteDatabase.query(mDbTableName, column, selection, selectionArgs, groupBy, having, orderBy, limit);
            while (cursor.moveToNext()) {
                Iterator<Map.Entry<String, Field>> entryIterator = mColumnCacheMap.entrySet().iterator();
                Object t = mEntity.newInstance();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, Field> entry = entryIterator.next();
                    Field field = entry.getValue();
                    field.setAccessible(true);
                    String columnName = entry.getKey();
                    Class<?> type = field.getType();
                    if (type == String.class) {
                        field.set(t, cursor.getString(cursor.getColumnIndex(columnName)));
                    } else if (type == Double.class) {
                        field.set(t, cursor.getDouble(cursor.getColumnIndex(columnName)));
                    } else if (type == Float.class) {
                        field.set(t, cursor.getFloat(cursor.getColumnIndex(columnName)));
                    } else if (type == int.class) {
                        field.set(t, cursor.getInt(cursor.getColumnIndex(columnName)));
                    } else if (type == Long.class) {
                        field.set(t, cursor.getLong(cursor.getColumnIndex(columnName)));
                    } else if (type == Short.class) {
                        field.set(t, cursor.getShort(cursor.getColumnIndex(columnName)));
                    } else if (type == byte[].class) {
                        field.set(t, cursor.getBlob(cursor.getColumnIndex(columnName)));
                    } else if (type == Serializable.class) {
                        field.set(t, cursor.getBlob(cursor.getColumnIndex(columnName)));
                    }
                }
                if (t != null) {
                    list.add((T) t);
                }
            }
            return list;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return list;
        }
    }

    private String getColumnsType(Class<?> type) {
        String columnsType = null;
        if (type == String.class) {
            columnsType = " TEXT ";
        } else if (type == Double.class) {
            columnsType = " DOUBLE ";
        } else if (type == Float.class) {
            columnsType = " FLOAT ";
        } else if (type == int.class) {
            columnsType = " INT ";
        } else if (type == Long.class) {
            columnsType = " BIGINT ";
        } else if (type == Short.class) {
            columnsType = " SMALLINT ";
        } else if (type == byte[].class) {
            columnsType = " BLOB ";
        } else if (type == Serializable.class) {
            columnsType = " BLOB ";
        }
        return columnsType;
    }

    private Object getPrimaryKeyValue(T t) throws IllegalAccessException {
        Field primaryKeyField = mColumnCacheMap.get(primaryKey);
        Object primaryKeyValue = null;
        primaryKeyValue = primaryKeyField.get(t);
        return primaryKeyValue;
    }

    private ContentValues getContentValues(T entity) throws IllegalAccessException {
        ContentValues contentValues = new ContentValues();
        for (Map.Entry<String, Field> entry : mColumnCacheMap.entrySet()) {
            String column = entry.getKey();
            Field field = entry.getValue();
            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            if (fieldValue == null) {
                continue;
            }

            Class<?> type = field.getType();
            if (type == String.class) {
                contentValues.put(column, String.valueOf(fieldValue));
            } else if (type == Double.class) {
                contentValues.put(column, (double) fieldValue);
            } else if (type == Float.class) {
                contentValues.put(column, (Float) fieldValue);
            } else if (type == int.class) {
                contentValues.put(column, (int) fieldValue);
            } else if (type == Long.class) {
                contentValues.put(column, (Long) fieldValue);
            } else if (type == Short.class) {
                contentValues.put(column, (Short) fieldValue);
            } else if (type == byte[].class) {
                contentValues.put(column, (byte[]) fieldValue);
            } else if (type == Serializable.class) {
                // TODO 待实现
                //contentValues.put(column, (Serializable) fieldValue);
            }
        }
        return contentValues;
    }

    private static Map<String, LightSQLite> mLightSQLites = new HashMap<>();

    public synchronized static <T> LightSQLite<T> create(String dbName, Class<T> entity) {
        String key = dbName + entity.getName();
        LightSQLite<T> lightSQLite = mLightSQLites.get(key);
        if (null == lightSQLite) {
            lightSQLite = new LightSQLite<>(dbName, entity);
            mLightSQLites.put(key, lightSQLite);
        }
        return lightSQLite;
    }

    public static Object toObject(byte[] input) {
        if (input == null) return null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(input);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bais) {
                    bais.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != ois) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Column {
        String columns();

        boolean isNull() default true;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Id {
        String columns();

        boolean autoincrement() default true;
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Table {
        String value();
    }

}