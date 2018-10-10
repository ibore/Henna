package me.ibore.henna.download;

import me.ibore.henna.progress.ProgressListener;

public interface DownloadListener extends ProgressListener {

    void onQueue();

    void onStart();

    void onPause();

    void onCancel();

    void onError(Exception e);

    void onFinish();


}
