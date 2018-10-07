package me.ibore.henna.download;

public interface DownloadListener {

    void onQueue(DownloadTask downloadTask);

    void onConnecting(DownloadTask downloadTask);

    void onStart(DownloadTask downloadTask);

    void onPause(DownloadTask downloadTask);

    void onCancel(DownloadTask downloadTask);

    void onFinish(DownloadTask downloadTask);

    void onError(DownloadTask downloadTask, int code);


}
