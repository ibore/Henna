package me.ibore.http.download.db;


import java.util.List;

import me.ibore.http.download.download.DownloadManager;
import me.ibore.http.download.download.TaskEntity;
import me.ibore.http.download.download.TaskEntityDao;

/**
 * Created by Yuan on 8/17/16.
 * <p>
 * download database dao
 */

public class DaoManager {


    private static DaoManager mInstance;

    private DaoManager() {
    }

    public static DaoManager instance() {
        synchronized (DaoManager.class) {
            if (mInstance == null) {
                mInstance = new DaoManager();
            }
        }
        return mInstance;
    }

    public void insertOrReplace(TaskEntity entity) {
        DownloadManager.getInstance().getDaoSession().insertOrReplace(entity);
    }

    public TaskEntity queryWidthId(String taskId) {
        return DownloadManager.getInstance().getDaoSession().getTaskEntityDao().queryBuilder().where(TaskEntityDao.Properties.TaskId.eq(taskId)).unique();
    }

    public List<TaskEntity> queryAll() {
        return DownloadManager.getInstance().getDaoSession().getTaskEntityDao().loadAll();
    }

    public void update(TaskEntity entity) {
        TaskEntityDao taskEntityDao = DownloadManager.getInstance().getDaoSession().getTaskEntityDao();
        if(taskEntityDao.hasKey(entity)) {
            taskEntityDao.update(entity);
        }
    }

    public void delete(TaskEntity entity) {
        DownloadManager.getInstance().getDaoSession().delete(entity);
    }

}
