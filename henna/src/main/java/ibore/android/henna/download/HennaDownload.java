package ibore.android.henna.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.ibore.http.BuildConfig;
import okhttp3.OkHttpClient;

public class HennaDownload {

    // manager instance
    private static HennaDownload mInstance;

    // quess
    private LinkedBlockingQueue<Runnable> mQueue;

    // ok http
    private OkHttpClient mClient;

    // ThreadPoolExecutor
    private ThreadPoolExecutor mExecutor;

    // the thread count
    private int mThreadCount = 1;

    // task list
    private Map<String, DownloadTask> mCurrentTaskList;

    private final int MAX_THREAD_COUNT = 15;

    private HennaDownload() {

    }

    public static synchronized HennaDownload getInstance() {
        if (mInstance == null) {
            mInstance = new HennaDownload();
        }
        return mInstance;
    }

    /**
     * @param context Application
     */
    public void init(@NonNull Context context) {
        init(context, getAppropriateThreadCount());
    }

    /**
     * @param context     Application
     * @param threadCount the max download count
     */
    public void init(@NonNull Context context, int threadCount) {
        init(context, threadCount, getOkHttpClient());
    }

    /**
     * @param context     Application
     * @param threadCount the max download count
     * @param client      okhttp client
     */

    public void init(@NonNull Context context, int threadCount, @NonNull OkHttpClient client) {
        recoveryTaskState();
        mClient = client;
        mThreadCount = threadCount < 1 ? 1 : threadCount <= MAX_THREAD_COUNT ? threadCount : MAX_THREAD_COUNT;
        mExecutor = new ThreadPoolExecutor(mThreadCount, mThreadCount, 20, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mCurrentTaskList = new HashMap<>();
        mQueue = (LinkedBlockingQueue<Runnable>) mExecutor.getQueue();


    }

    /**
     * generate default client
     */
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
    }

    /**
     * @return generate the appropriate thread count.
     */
    private int getAppropriateThreadCount() {
        return Runtime.getRuntime().availableProcessors() * 2 + 1;
    }

    /**
     * add task
     */
    public void addTask(@NonNull DownloadTask task) {

        Download download = task.getDownload();

        if (download != null && download.getTaskStatus() != DownloadTaskStatus.TASK_STATUS_DOWNLOADING) {
            task.setClient(mClient);
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
    public void pauseTask(@NonNull DownloadTask task) {
        if (mQueue.contains(task)) {
            mQueue.remove(task);
        }
        task.pause();
    }

    /**
     * resumeTask task
     */
    public void resumeTask(@NonNull DownloadTask task) {
        addTask(task);
    }


    /**
     * cancel task
     */
    public void cancelTask(DownloadTask task) {
        if(task == null) return;
        Download download = task.getDownload();
        if (download != null) {
            if(task.getDownload().getTaskStatus() == DownloadTaskStatus.TASK_STATUS_DOWNLOADING){
                pauseTask(task);
                mExecutor.remove(task);
            }

            if (mQueue.contains(task)) {
                mQueue.remove(task);
            }
            mCurrentTaskList.remove(download.getTaskId());
            task.cancel();
            if (!TextUtils.isEmpty(download.getFilePath()) && !TextUtils.isEmpty(download.getFileName())) {
                File temp = new File(download.getFilePath(), download.getFileName());
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
    public DownloadTask getTask(String id) {
        DownloadTask currTask = mCurrentTaskList.get(id);
        if (currTask == null) {
            Download entity = DownloadDb.queryWidthId(id);
            if (entity != null) {
                int status = entity.getTaskStatus();
                currTask = new DownloadTask(entity);
                if (status != DownloadTaskStatus.TASK_STATUS_FINISH) {
                    mCurrentTaskList.put(id, currTask);
                }
            }
        }
        return currTask;
    }


    public boolean isPauseTask(String id) {
        Download download = DownloadDb.queryWidthId(id);
        if (download != null) {
            File file = new File(download.getFilePath(), download.getFilePath());
            if (file.exists()) {
                long totalSize = download.getTotalSize();
                return totalSize > 0 && file.length() < totalSize;
            }
        }
        return false;
    }

    public boolean isFinishTask(String id) {
        Download download = DownloadDb.queryWidthId(id);
        if (download != null) {
            File file = new File(download.getFilePath(), download.getFileName());
            if (file.exists()) {
                return file.length() == download.getTotalSize();
            }
        }
        return false;
    }

    private void recoveryTaskState() {
        List<Download> downloads = DownloadDb.queryAll();
        for (Download download : downloads) {
            long completedSize = download.getCompletedSize();
            long totalSize = download.getTotalSize();
            if (completedSize > 0 && completedSize != totalSize && download.getTaskStatus() != DownloadTaskStatus.TASK_STATUS_PAUSE) {
                download.setTaskStatus(DownloadTaskStatus.TASK_STATUS_PAUSE);
            }
            DownloadDb.update(download);
        }
    }

}
