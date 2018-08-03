package ibore.android.henna.download;

public interface DownloadTaskListener {


    void onQueue(DownloadTask downloadTask);

    /**
     * connecting
     */
    void onConnecting(DownloadTask downloadTask);

    /**
     * downloading
     */
    void onStart(DownloadTask downloadTask);

    /**
     * pauseTask
     */
    void onPause(DownloadTask downloadTask);

    /**
     * cancel
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * success
     */
    void onFinish(DownloadTask downloadTask);

    /**
     * failure
     */
    void onError(DownloadTask downloadTask, int code);

}
