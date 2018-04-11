package me.ibore.http;

import me.ibore.http.progress.ProgressInfo;
import me.ibore.http.progress.ProgressListener;

/**
 * Created by Administrator on 2018/2/6.
 */

public abstract class DownloadObserver extends HttpObserver<HttpInfo> implements HttpListener<DownloadInfo>, ProgressListener {

    @Override
    public void onNext(HttpInfo httpInfo) {
        onProgress(httpInfo.getProgressInfo());
        if (httpInfo.getProgressInfo().getCurrent() == httpInfo.getProgressInfo().getTotal()) {
            onSuccess((DownloadInfo) httpInfo.getResponseInfo());
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
