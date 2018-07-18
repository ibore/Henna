package me.ibore.http.listener;

import me.ibore.http.progress.ProgressListener;

public abstract class HennaListener<T> implements HttpListener<T>, ProgressListener {

    public void onStart() {

    }

    public void onFinish() {

    }
}
