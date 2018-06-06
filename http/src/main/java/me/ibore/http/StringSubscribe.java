package me.ibore.http;

import java.io.InputStream;
import java.util.Arrays;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import me.ibore.http.exception.HttpException;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/6.
 */

public class StringSubscribe implements ObservableOnSubscribe<HttpInfo> {

    private HttpInfo<StringInfo> httpInfo;

    public StringSubscribe(HttpInfo<StringInfo> httpInfo) {
        this.httpInfo = httpInfo;
    }

    @Override
    public void subscribe(ObservableEmitter<HttpInfo> observer) {
        InputStream inputStream = null;
        try {
            RequestInfo requestInfo = httpInfo.getRequestInfo();
            Progress progressInfo = httpInfo.getProgressInfo();
            StringInfo stringInfo = httpInfo.getResponseInfo();
            Request request = new Request.Builder()
                    .url(requestInfo.getUrl())
                    .tag(requestInfo.getUrl())
                    .build();
            Response response = XHttp.getOkHttpClient().newCall(request).execute();
            progressInfo.setTotal(response.body().contentLength());
            if (response.isSuccessful()) {
                long bytesWritten = 0;
                inputStream = response.body().byteStream();
                StringBuilder stringBuilder = new StringBuilder();
                byte[] buffer = new byte[2048];
                int len;
                long lastRefreshUiTime = 0;
                long lastWriteBytes = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    stringBuilder.append(Arrays.toString(buffer));
                    bytesWritten += len;
                    long curTime = System.currentTimeMillis();
                    if (curTime - lastRefreshUiTime >= XHttp.REFRESH_TIME || bytesWritten == progressInfo.getTotal()) {
                        long diffTime = (curTime - lastRefreshUiTime) / 1000;
                        if (diffTime == 0) diffTime += 1;
                        long diffBytes = bytesWritten - lastWriteBytes;
                        final long networkSpeed = diffBytes / diffTime;
                        lastRefreshUiTime = System.currentTimeMillis();
                        lastWriteBytes = bytesWritten;
                        progressInfo.setSpeed(networkSpeed);
                        progressInfo.setCurrent(bytesWritten);
                        progressInfo.setProgress((int) (bytesWritten * 10000 / progressInfo.getTotal()));
                        stringInfo.setData(stringBuilder.toString());
                        observer.onNext(httpInfo);
                    }
                }
            } else {
                observer.onError(new HttpException(response.code(), response.message()));
            }
        } catch (Exception e) {
            observer.onError(e);
        } finally {
            Utils.closeIO(inputStream);
        }
        observer.onComplete();
    }

}
