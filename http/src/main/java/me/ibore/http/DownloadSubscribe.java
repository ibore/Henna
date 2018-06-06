package me.ibore.http;

import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import me.ibore.http.exception.HttpException;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ibore on 18-2-6.
 */

public class DownloadSubscribe implements ObservableOnSubscribe<HttpInfo> {

    private HttpInfo<DownloadInfo> httpInfo;

    public DownloadSubscribe(HttpInfo<DownloadInfo> httpInfo) {
        this.httpInfo = httpInfo;
    }

    @Override
    public void subscribe(ObservableEmitter<HttpInfo> observer) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            RequestInfo requestInfo = httpInfo.getRequestInfo();
            Progress progressInfo = httpInfo.getProgressInfo();
            DownloadInfo downloadInfo  = httpInfo.getResponseInfo();
            if (progressInfo.getCurrent() == progressInfo.getTotal()) {
                observer.onNext(httpInfo);
            } else {
                if (progressInfo.getCurrent() > progressInfo.getTotal()) {
                    progressInfo.setCurrent(0);
                    downloadInfo.getFile().createNewFile();
                }
                long bytesWritten = progressInfo.getCurrent();
                long contentLength = progressInfo.getTotal();
                observer.onNext(httpInfo);
                Request request = new Request.Builder()
                        .addHeader("RANGE", "bytes=" + bytesWritten + "-" + contentLength)
                        .url(requestInfo.getUrl())
                        .tag(requestInfo.getUrl())
                        .build();
                Response response  =  XHttp.getOkHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    inputStream = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(downloadInfo.getFile(), true);
                    byte[] buffer = new byte[2048];//缓冲数组2kB
                    int len;
                    long lastRefreshUiTime = 0;
                    long lastWriteBytes = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        bytesWritten += len;
                        long curTime = System.currentTimeMillis();

                        if (curTime - lastRefreshUiTime >= XHttp.REFRESH_TIME || bytesWritten == contentLength) {
                            long diffTime = (curTime - lastRefreshUiTime) / 1000;
                            if (diffTime == 0) diffTime += 1;
                            long diffBytes = bytesWritten - lastWriteBytes;
                            final long networkSpeed = diffBytes / diffTime;
                            lastRefreshUiTime = curTime;
                            lastWriteBytes = bytesWritten;
                            progressInfo.setSpeed(networkSpeed);
                            progressInfo.setCurrent(bytesWritten);
                            progressInfo.setProgress((int) (bytesWritten * 10000 / contentLength));
                            observer.onNext(httpInfo);
                        }
                    }
                    fileOutputStream.flush();
                } else {
                    observer.onError(new HttpException(response.code(), response.message()));
                }
            }
        } catch (Exception e) {
            observer.onError(e);
        } finally {
            Utils.closeIO(inputStream, fileOutputStream);
        }
        observer.onComplete();//完成
    }
}
