package me.ibore.henna.download;

import java.util.List;

import me.ibore.henna.db.DownloadDao;
import me.ibore.henna.db.HennaDb;


public class DownloadDb {

    public static void insertOrReplace(Download download) {
        HennaDb.getDownloadDao().insertOrReplace(download);
    }

    public static Download queryWidthId(String taskId) {
        return HennaDb.getDownloadDao().queryBuilder().where(DownloadDao.Properties.TaskId.eq(taskId)).unique();
    }

    public static List<Download> queryAll() {
        return HennaDb.getDownloadDao().loadAll();
    }

    public static void update(Download entity) {
        DownloadDao taskEntityDao = HennaDb.getDownloadDao();
        if(taskEntityDao.hasKey(entity)) {
            taskEntityDao.update(entity);
        }
    }

    public static void delete(Download download) {
        HennaDb.getDownloadDao().delete(download);
    }

}
