package me.ibore.http;

import me.ibore.http.progress.ProgressInfo;

/**
 * Created by Administrator on 2018/2/6.
 */

public class HttpInfo<T> {

    private ProgressInfo progressInfo;
    private RequestInfo requestInfo;
    private T responseInfo;

    public ProgressInfo getProgressInfo() {
        return progressInfo;
    }

    public void setProgressInfo(ProgressInfo progressInfo) {
        this.progressInfo = progressInfo;
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public T getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(T responseInfo) {
        this.responseInfo = responseInfo;
    }
}
