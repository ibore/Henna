package me.ibore.http.callback;

import me.ibore.http.HttpListener;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.request.Request;

public interface Callback<T> extends HttpListener<T>, ProgressListener {

    /** 请求网络开始前，UI线程 */
    void onStart(Request<T, ? extends Request> request);


}
