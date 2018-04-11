package me.ibore.http;

import me.ibore.http.progress.ProgressInfo;
import me.ibore.http.progress.ProgressListener;

/**
 * Created by Administrator on 2018/2/7.
 */

public abstract class StringObserver extends HttpObserver<HttpInfo> implements HttpListener<StringInfo>, ProgressListener {

    @Override
    public void onNext(HttpInfo httpInfo) {
        onProgress(httpInfo.getProgressInfo());
        if (httpInfo.getProgressInfo().getCurrent() == httpInfo.getProgressInfo().getTotal()) {
            onSuccess((StringInfo) httpInfo.getResponseInfo());
        }
    }

    @Override
    public void onProgress(ProgressInfo progressInfo) {

    }

    @Override
    public void onError(Throwable e) {
        Utils.OnError(this, e);
    }
}