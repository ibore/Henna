package me.ibore.henna.download;


import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.ibore.henna.BuildConfig;
import me.ibore.henna.Henna;
import me.ibore.henna.db.LightSQLite;

public final class HennaDownload {

    private final int MAX_THREAD_COUNT = 15;
    private Henna mHenna;
    private LinkedBlockingQueue<Runnable> mQueue;
    private String mFileDir;
    private ThreadPoolExecutor mExecutor;
    private int mThreadCount;
    private Map<Long, DownloadTask> mCurrentTaskList;
    private LightSQLite<Download> mSQLite;

    private HennaDownload(Henna henna, String fileDir, int threadCount) {
        this.mHenna = henna;
        this.mFileDir = fileDir;
        this.mThreadCount = threadCount;
        mSQLite = LightSQLite.create(mHenna.context().getDatabasePath("henna.db").getAbsolutePath(), Download.class);
        mThreadCount = threadCount < 1 ? 1 : threadCount <= MAX_THREAD_COUNT ? threadCount : MAX_THREAD_COUNT;
        mExecutor = new ThreadPoolExecutor(mThreadCount, mThreadCount, 20, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mCurrentTaskList = new HashMap<>();
        mQueue = (LinkedBlockingQueue<Runnable>) mExecutor.getQueue();
    }

    public Henna getHenna() {
        return mHenna;
    }

    public String getFileDir() {
        return mFileDir;
    }

    public LightSQLite<Download> getSQLite() {
        return mSQLite;
    }

    /**
     * add task
     */
    public void addTask(DownloadTask task) {
        Download download = task.getDownload();
        if (download != null && download.getTaskStatus() != Download.DOWNLOADING) {
            download.setFileDir(mFileDir);
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

    /**
     * cancel task
     */
    public void cancelTask(DownloadTask task) {
        if (task == null) return;
        Download download = task.getDownload();
        if (download != null) {
            if (task.getDownload().getTaskStatus() == Download.DOWNLOADING) {
                pauseTask(task);
                mExecutor.remove(task);
            }
            if (mQueue.contains(task)) {
                mQueue.remove(task);
            }
            mCurrentTaskList.remove(download.getTaskId());
            task.cancel();
            if (!TextUtils.isEmpty(download.getFileDir()) && !TextUtils.isEmpty(download.getFileName())) {
                File temp = new File(download.getFileDir(), download.getFileName());
                if (temp.exists()) {
                    if (temp.delete()) {
                        if (BuildConfig.DEBUG) Log.d("DownloadManager", "delete temp file!");
                    }
                }
            }
        }
    }

    /**
     * @return task
     */
    public DownloadTask getTask(Long taskId) {
        DownloadTask currTask = mCurrentTaskList.get(taskId);
        if (currTask == null) {
            Download download = mSQLite.queryById(taskId);
            if (download != null) {
                int status = download.getTaskStatus();
                currTask = new DownloadTask(download);
                if (status != Download.FINISH) {
                    mCurrentTaskList.put(taskId, currTask);
                }
            }
        }
        return currTask;
    }


    public boolean isPauseTask(Long taskId) {
        Download download = mSQLite.queryById(taskId);
        if (download != null) {
            File file = new File(download.getFileDir(), download.getFileName());
            if (file.exists()) {
                long contentLength = download.getContentLength();
                return contentLength > 0 && file.length() < contentLength;
            }
        }
        return false;
    }

    public boolean isFinishTask(Long taskId) {
        Download download = mSQLite.queryById(taskId);
        if (download != null) {
            File file = new File(download.getFileDir(), download.getFileName());
            if (file.exists()) {
                return file.length() == download.getContentLength();
            }
        }
        return false;
    }

    private void recoveryTaskState() {
        List<Download> downloads = mSQLite.queryAll();
        for (Download download : downloads) {
            long currentBytes = download.getCurrentBytes();
            long contentLength = download.getContentLength();
            if (currentBytes > 0 && currentBytes != contentLength && download.getTaskStatus() != Download.PAUSE) {
                download.setTaskStatus(Download.PAUSE);
            }
            mSQLite.update(download);
        }
    }

    /**
     * @return generate the appropriate thread count.
     */
    private int getAppropriateThreadCount() {
        return Runtime.getRuntime().availableProcessors() * 2 + 1;
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
