package me.ibore.http.listener;

import me.ibore.http.progress.Progress;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.request.Request;
import okhttp3.ResponseBody;

public abstract class AbsHttpListener<T> implements HttpListener<T>, ProgressListener {

    protected void onStart(Request request) { }

    public abstract T convert(ResponseBody responseBody) throws Exception;

    @Override
    public void onProgress(Progress progress) { }

    protected void onFinish() {}

}
