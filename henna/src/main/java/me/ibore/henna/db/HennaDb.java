package me.ibore.henna.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class HennaDb {

    private static DaoSession mDaoSession;

    public static void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "henna.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        mDaoSession = master.newSession();
    }

    public static DownloadDao getDownloadDao() {
        return mDaoSession.getDownloadDao();
    }


}
