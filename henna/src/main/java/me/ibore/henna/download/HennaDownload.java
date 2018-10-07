package me.ibore.henna.download;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.ibore.henna.Henna;
import me.ibore.henna.db.LightSQLite;

public final class HennaDownload {

    private Henna mHenna;
    private LightSQLite<Download> mSQLite;

    private final int MAX_THREAD_COUNT = 15;
    private LinkedBlockingQueue<Runnable> mQueue;

    private HennaDownload(Henna henna, String fileDir, int threadCount) {
        this.mHenna = henna;
        this.mFileDir = fileDir;
        this.mThreadCount = threadCount;
        mThreadCount = threadCount < 1 ? 1 : threadCount <= MAX_THREAD_COUNT ? threadCount : MAX_THREAD_COUNT;
        mExecutor = new ThreadPoolExecutor(mThreadCount, mThreadCount, 20, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mCurrentTaskList = new HashMap<>();
        mQueue = (LinkedBlockingQueue<Runnable>) mExecutor.getQueue();
        mSQLite = LightSQLite.create("henna.db", Download.class);
    }



    public Henna getHenna() {
        return mHenna;
    }

    public LightSQLite<Download> getSQLite() {
        return mSQLite;
    }

    public String getFileDir() {
        return mFileDir;
    }

    private String mFileDir;

    private ThreadPoolExecutor mExecutor;

    private int mThreadCount = 1;

    private Map<String, DownloadTask> mCurrentTaskList;

    /**
     * add task
     */
    public void addTask(DownloadTask task) {
        Download download = task.getDownload();
        if (download != null && download.getStatus() != DownloadTaskStatus.TASK_STATUS_DOWNLOADING) {
            task.setHennaDownload(this);
            mCurrentTaskList.put(download.getTaskId(), task);
            if (!mQueue.contains(task)) {
                mExecutor.execute(task);
            }

            if (mExecutor.getTaskCount() > mThreadCount) {
                task.queue();
            }
        }
    }

    /**
     * pauseTask task
     */
    public void pauseTask(DownloadTask task) {
        if (mQueue.contains(task)) {
            mQueue.remove(task);
        }
        task.pause();
    }

    /**
     * resumeTask task
     */
    public void resumeTask(DownloadTask task) {
        addTask(task);
    }

    public class Builder {

        private Henna mHenna;
        private String mFileDir;
        private int mThreadCount;

        public Builder() {

        }

        public Builder(HennaDownload hennaDownload) {
            mHenna = hennaDownload.mHenna;
            mThreadCount = hennaDownload.mThreadCount;
        }

        public Builder henna(Henna henna) {
            this.mHenna = henna;
            return this;
        }

        public Builder fileDir(String fileDir) {
            this.mFileDir = fileDir;
            return this;
        }

        public Builder threadCount(int threadCount) {
            this.mThreadCount = threadCount;
            return this;
        }

        public HennaDownload builder() {
            if (null == mHenna) {
                mHenna = new Henna.Builder().builder();
            }
            return new HennaDownload(mHenna, mFileDir, mThreadCount);
        }
    }
}
