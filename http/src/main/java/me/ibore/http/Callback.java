package me.ibore.http;

import me.ibore.http.request.Request;

public interface Callback<T> {

    /** 请求网络开始前，UI线程 */
    void onStart(Request<T, ? extends Request> request);

}
