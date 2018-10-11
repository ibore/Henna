package me.ibore.henna.download;

public interface DownloadListener {

    void onQueue();

    void onStart();

    void onDownloading(Download download);

    void onPause();

    void onCancel();

    void onError(Exception e);

    void onFinish();


}
