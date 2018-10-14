package me.ibore.henna.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Table<T> {

    public static final int EXCEPTION_CODE = -1;

    private DBHelper mHelper;

    protected void setDBHelper(DBHelper helper) {
        mHelper = helper;
    }

    private DBHelper.DBHelperProxy getDataBase() {
        if (mHelper == null) {
            mHelper = DBHelper.getDBHelper(getTableName());
            if (mHelper == null) {
                throw new NullPointerException("Database not open or current table not in getTables() method !!");
            }
        }
        return mHelper.getDBProxy();
    }

    /**
     * 创建触发器
     *
     * @param db
     *            SQLiteDatabase
     */
    public void onCreateTrigger(SQLiteDatabase db) {

    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<T> queryAll() {
        return query(null, null, null, null, null, null, null);
    }

    /**
     * 查询符合条件的数据
     *
     * @param where
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     *            if <= 0，返回所有
     * @return
     */
    public List<T> query(String where, String groupBy, String having, String orderBy, int limit) {
        if (limit <= 0) {
            return query(null, where, groupBy, having, orderBy, null, null);
        }
        return query(null, where, groupBy, having, orderBy, limit + "", null);
    }

    /**
     * 查询符合条件的数据
     *
     * @param where
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public List<T> query(String where, String groupBy, String having, String orderBy) {
        return query(null, where, groupBy, having, orderBy, null, null);
    }

    /**
     * 查询符合条件的记录的某些字段数据
     *
     * @param columns
     * @param where
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public List<T> query(String[] columns, String where, String groupBy, String having, String orderBy, int limit) {
        if (limit <= 0) {
            return query(columns, where, groupBy, having, orderBy, null, null);
        }
        return query(columns, where, groupBy, having, orderBy, limit + "", null);
    }

    /**
     * 查询符合条件的记录的某些字段数据
     *
     * @param columns
     * @param where
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public List<T> query(String[] columns, String where, String groupBy, String having, String orderBy) {
        return query(columns, where, groupBy, having, orderBy, null, null);
    }

    /**
     * 返回符合条件的一条数据
     *
     * @param where
     * @return
     */
    public T queryOne(String where) {
        List<T> data = query(null, where, null, null, null, "1", null);
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    /**
     * 返回符合条件的一条数据某些字段
     *
     * @param columns
     * @param where
     * @return
     */
    public T queryOne(String[] columns, String where) {
        List<T> data = query(columns, where, null, null, null, "1", null);
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    /**
     * 返回符合条件的一条数据某些字段
     *
     * @param columns
     * @param where
     * @param orderBy
     * @return
     */
    public T queryOne(String[] columns, String where, String orderBy) {
        List<T> data = query(columns, where, null, null, orderBy, "1", null);
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    /**
     * 查询符合条件的数据
     *
     * @param where
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     *            　格式：limit s,c
     * @return return null if Exception
     */
    private List<T> query(String[] columns, String where, String groupBy, String having, String orderBy, String limit,
                         String[] whereArgs) {
        Cursor cur = null;
        List<T> list = new ArrayList<T>();
        try {
            SQLiteDatabase db = getDataBase().getReadableDatabase();
            cur = db.query(false, getTableName(), columns, where, whereArgs, groupBy, having, orderBy, limit);
            if (cur == null) {
                return list;
            }

            int count = cur.getCount();
            cur.moveToFirst();
            for (int i = 0; i < count; i++) {
                Object obj = null;
                Type t = getClass().getGenericSuperclass();
                if (t != null && t instanceof ParameterizedType) {
                    Type[] type = ((ParameterizedType) t).getActualTypeArguments();
                    Class<?> c = (Class<?>) type[0];
                    try {
                        obj = c.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("未找到" + c.getName()+ " 默认构造方法，或构造方法非public");
                    }
                    Field[] fields = c.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.isAnnotationPresent(Column.class)) {
                            Column an = field.getAnnotation(Column.class);
                            int index = cur.getColumnIndex(an.name());
                            if (index != -1) {
                                Class<?> fieldType = field.getType();
                                if (fieldType == String.class || fieldType == char.class) {
                                    field.set(obj, cur.getString(index));
                                } else if (fieldType == int.class) {
                                    field.set(obj, cur.getInt(index));
                                } else if (fieldType == byte.class) {
                                    field.set(obj, (byte)cur.getInt(index));
                                } else if (fieldType == long.class) {
                                    field.set(obj, cur.getLong(index));
                                } else if (fieldType == float.class) {
                                    field.set(obj, cur.getFloat(index));
                                } else if (fieldType == double.class) {
                                    field.set(obj, cur.getDouble(index));
                                } else if (fieldType == short.class) {
                                    field.set(obj, cur.getShort(index));
                                } else if (fieldType == byte[].class) {
                                    field.set(obj, (byte[])cur.getBlob(index));
                                }
                            }
                        }
                    }

                }
                list.add((T) obj);
                cur.moveToNext();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return list;
    }

    /**
     * 插入数据
     *
     * @param data
     *            数据源
     * @return　成功插入记录的行号,插入失败返回-1,异常返回{@link #EXCEPTION_CODE}
     */
    public long insert(T data) {
        long row = -1;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = createContentValues(data);
            row = db.insert(getTableName(), null, values);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    private ContentValues createContentValues(T data) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                Column an = field.getAnnotation(Column.class);
                Class<?> c = field.getType();
                if (c == String.class || c == char.class) {
                    values.put(an.name(), (String) field.get(data));
                } else if (c == int.class) {
                    values.put(an.name(), field.getInt(data));
                } else if (c == long.class) {
                    values.put(an.name(), field.getLong(data));
                } else if (c == byte.class) {
                    values.put(an.name(), field.getByte(data));
                } else if (c == short.class) {
                    values.put(an.name(), field.getShort(data));
                } else if (c == float.class) {
                    values.put(an.name(), field.getFloat(data));
                } else if (c == double.class) {
                    values.put(an.name(), field.getDouble(data));
                } else if (c == byte[].class) {
                    values.put(an.name(), (byte[])field.get(data));
                } else {
                    throw new RuntimeException(field.getName() + " not allow " + field.getType().getName());
                }
            }
        }
        return values;
    }

    /**
     * @param data
     *            数据源
     * @return 成功插入记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int insert(List<T> data) {
        return insert(data, false);
    }

    /**
     * @param data 数据源
     * @param isBeginTransaction 是否开启事务
     * @return 成功插入记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int insert(List<T> data, boolean isBeginTransaction) {
        int insertNum = 0;
        SQLiteDatabase db = null;
        try {
            db = getDataBase().getWritableDatabase();
            if (isBeginTransaction)
                db.beginTransaction();
            int count = data.size();
            insertNum = 0;
            for (int i = 0; i < count; i++) {
                ContentValues values = createContentValues(data.get(i));
                long row = db.insert(getTableName(), null, values);
                if (row != -1)
                    insertNum++;
            }
            if (isBeginTransaction)
                db.setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        } finally {
            if (isBeginTransaction && db != null) {
                db.endTransaction();
            }
        }
        return insertNum;
    }

    /**
     * 更新全部记录
     *
     * @param data
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(T data) {
        return update(data, null);
    }

    /**
     * 更新符合条件的记录
     *
     * @param data
     *            数据源
     * @param where
     *            条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(T data, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = createContentValues(data);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field [字段1,字段2,...]
     * @param value [值1,值2,...]
     * @param where 条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String where, String field[], Object... value) {
        if (null != value && value.length == 0) {
            return 0;
        }
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            for (int i = 0; i < field.length; i++) {
                if (value[i] instanceof Integer) {
                    values.put(field[i], (Integer) value[i]);
                } else if (value[i] instanceof Byte) {
                    values.put(field[i], (Byte) value[i]);
                } else if (value[i] instanceof Long) {
                    values.put(field[i], (Long) value[i]);
                } else if (value[i] instanceof Double) {
                    values.put(field[i], (Double) value[i]);
                } else if (value[i] instanceof Float) {
                    values.put(field[i], (Float) value[i]);
                } else if (value[i] instanceof Boolean) {
                    values.put(field[i], (Boolean) value[i]);
                } else if (value[i] instanceof byte[]) {
                    values.put(field[i], (byte[]) value[i]);
                } else {
                    values.put(field[i], (String) value[i]);
                }
            }
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field 字段
     * @param value 值
     * @param where 条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, String value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field 字段
     * @param value 值
     * @param where 条件
     * @return
     */
    public int update(String field, int value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field 字段
     * @param value 值
     * @param where 条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, boolean value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field
     *            字段
     * @param value
     *            值
     * @param where
     *            条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, float value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field
     *            字段
     * @param value
     *            值
     * @param where
     *            条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, double value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field
     *            字段
     * @param value
     *            值
     * @param where
     *            条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, long value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field
     *            字段
     * @param value
     *            值
     * @param where
     *            条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, byte value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 更新符合条件的数据
     *
     * @param field
     *            字段
     * @param value
     *            值
     * @param where
     *            条件
     * @return 成功更新记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int update(String field, byte[] value, String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(field, value);
            row = db.update(getTableName(), values, where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 删除符合条件的数据
     *
     * @param where
     * @return 成功删除记录的数量, 异常返回{@link #EXCEPTION_CODE}
     */
    public int remove(String where) {
        int row = 0;
        try {
            SQLiteDatabase db = getDataBase().getReadableDatabase();
            row = db.delete(getTableName(), where, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return EXCEPTION_CODE;
        }
        return row;
    }

    /**
     * 删除全部数据
     */
    public int removeAll() {
        return remove(null);
    }

    /**
     * 执行原生SQL查询操作
     *
     * @return
     */
    public Cursor exeRawSqlQuery(String sql) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getDataBase().getReadableDatabase();
            cursor = db.rawQuery(sql, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
        return cursor;
    }

    /**
     * 执行事务读写操作
     *
     * @param sql
     * @return
     */
    public boolean exeRawSqlTransaction(String sql) {
        try {
            SQLiteDatabase db = getDataBase().getWritableDatabase();
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static Integer[] box(int[] src) {
        if (null == src) {
            return null;
        }
        Integer[] dest = new Integer[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = src[i];
        }
        return dest;
    }

    private static Long[] box(long[] src) {
        if (null == src) {
            return null;
        }
        Long[] dest = new Long[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = src[i];
        }
        return dest;
    }

    private static Float[] box(float[] src) {
        if (null == src) {
            return null;
        }
        Float[] dest = new Float[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = src[i];
        }
        return dest;
    }

    private static Double[] box(double[] src) {
        if (null == src) {
            return null;
        }
        Double[] dest = new Double[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = src[i];
        }
        return dest;
    }

    private static String getBatchWhereClause(String field, Object... inArgs) {
        if (null == inArgs) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (inArgs.length == 1) {
            Object arg = inArgs[0];
            if (arg instanceof int[]) {
                Integer[] argT = box((int[]) arg);
                sb.append(field).append(" IN(");
                append(sb, argT);
                sb.append(')');
            } else if (arg instanceof long[]) {
                Long[] argT = box((long[]) arg);
                sb.append(field).append(" IN(");
                append(sb, argT);
                sb.append(')');
            } else if (arg instanceof double[]) {
                Double[] argT = box((double[]) arg);
                sb.append(field).append(" IN(");
                append(sb, argT);
                sb.append(')');
            } else if (arg instanceof float[]) {
                Float[] argT = box((float[]) arg);
                sb.append(field).append(" IN(");
                append(sb, argT);
                sb.append(')');
            } else {
                sb.append(field);
                sb.append('=');
                append(sb, arg);
            }
        } else {
            sb.append(field).append(" IN(");
            append(sb, inArgs);
            sb.append(')');
        }
        return sb.toString();
    }

    private static void append(StringBuilder sb, Object[] inArgs) {
        if (null == inArgs) {
            return;
        }
        boolean isFirst = true;
        for (Object arg : inArgs) {
            if (arg == null) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(',');
            }
            append(sb, arg);
        }
    }

    private static void append(StringBuilder sb, Object inArgs) {
        if (null == inArgs) {
            return;
        }
        if (inArgs instanceof Integer) {
            sb.append((Integer) inArgs);
        } else if (inArgs instanceof String) {
            sb.append("'");
            sb.append((String) inArgs);
            sb.append("'");
        } else if (inArgs instanceof Long) {
            sb.append((Long) inArgs);
        } else if (inArgs instanceof Float) {
            sb.append((Float) inArgs);
        } else if (inArgs instanceof Double) {
            sb.append((Double) inArgs);
        } else if (inArgs instanceof Byte) {
            sb.append((Byte) inArgs);
        } else if (inArgs instanceof Boolean) {
            sb.append((Boolean) inArgs);
        } else if (inArgs instanceof Character) {
            sb.append("'");
            sb.append((Character) inArgs);
            sb.append("'");
        } else if (inArgs instanceof Object[]) {
            append(sb, (Object[]) inArgs);
        } else {
            sb.append("'");
            sb.append(inArgs.toString());
            sb.append("'");
        }
    }

    /**
     * 获得表名
     *
     * @return　表名
     */
    public abstract String getTableName();

    /**
     * 数据库有更新时调用
     *
     * @param db
     *            SQLiteDatabase
     */
    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);


}
