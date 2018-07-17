package me.ibore.http.listener;

import me.ibore.http.progress.Progress;
import me.ibore.http.progress.ProgressListener;

public abstract class HennaListener<T> implements HttpListener<T>, ProgressListener {

    public void onStart() {

    }

    @Override
    public void onProgress(Progress progress) {

    }

    public void onFinish() {

    }
}
