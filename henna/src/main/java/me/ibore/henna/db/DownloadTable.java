package me.ibore.henna.db;

import android.database.sqlite.SQLiteDatabase;

import me.ibore.henna.download.Download;

public class DownloadTable extends Table<Download> {

    private final String TABLE_NAME = "download";

    private static DownloadTable sInstance;

    private DownloadTable(){}

    public synchronized static DownloadTable getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadTable();
        }
        return sInstance;
    }


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
