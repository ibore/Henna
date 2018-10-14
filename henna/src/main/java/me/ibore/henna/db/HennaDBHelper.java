package me.ibore.henna.db;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class HennaDBHelper extends DBHelper {

    // 数据库名
    private final String DB_NAME = "henna.db";

    // 数据库版本
    // [VR = 1 数据库初版]
    // [VR = 2 版本号说明]
    // [VR = 3 版本号说明]
    // [...]
    private final int DB_VERSION = 1;

    private static HennaDBHelper mHennaDBHelper;

    public static void init(Context context) {
        mHennaDBHelper = new HennaDBHelper(context);
    }

    public static HennaDBHelper getInstance() {
        synchronized (mHennaDBHelper) {
            if (null == mHennaDBHelper) {
                throw new NullPointerException("please init")
            }
        }
        return mHennaDBHelper;
    }


    public HennaDBHelper(Context context) {
        super(context);
    }

    @Override
    public String getDataBaseName() {
        return DB_NAME;
    }

    @Override
    public int getDataBaseVersion() {
        return DB_VERSION;
    }

    @Override
    public List<Table<?>> getTables() {
        List<Table<?>> list = new ArrayList<>();
        list.add(DownloadTable.getInstance());
        //后续有新表继续添加
        //.........
        return list;
    }
}
