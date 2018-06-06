package me.ibore.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import me.ibore.http.exception.HttpException;
import okhttp3.Request;
import okhttp3.Response;

import static me.ibore.http.XHttp.getOkHttpClient;

/**
 * Created by ibore on 18-2-7.
 */

class Utils {

    public static void CHECKNULL(Object object) {
        if (null == object) {
            throw new NullPointerException("please init XHttp");
        }
    }

    public static HttpInfo createDownloadInfo(String url, File fileDirs) throws IOException {
        // 创建HttpInfo
        HttpInfo<DownloadInfo> httpInfo = new HttpInfo<>();
        // 创建RequestInfo
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUrl(url);
        // 创建ProgressInfo
        Progress progressInfo = new Progress();
        progressInfo.setMode(Progress.DOWNLOAD);
        progressInfo.setUrl(url);
        progressInfo.setTotal(getContentLength(url));
        // 创建DownloadInfo
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setUrl(url);
        downloadInfo.setFileName(url.substring(url.lastIndexOf("/")));
        downloadInfo.setFile(new File(fileDirs, downloadInfo.getFileName()));
        // 如果当前已经下载过了，则设置当前进度
        if (downloadInfo.getFile().exists()) {
            progressInfo.setCurrent(downloadInfo.getFile().length());
        }
        httpInfo.setRequestInfo(requestInfo);
        httpInfo.setProgressInfo(progressInfo);
        httpInfo.setResponseInfo(downloadInfo);

        return httpInfo;
    }

    public static HttpInfo<StringInfo> createStringInfo(String url) {
        // 创建HttpInfo
        HttpInfo<StringInfo> httpInfo = new HttpInfo<>();
        // 创建RequestInfo
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUrl(url);
        // 创建ProgressInfo
        Progress progressInfo = new Progress();
        progressInfo.setMode(Progress.DOWNLOAD);
        progressInfo.setUrl(url);
        // 创建StringInfo
        StringInfo stringInfo = new StringInfo();
        stringInfo.setUrl(url);
        httpInfo.setRequestInfo(requestInfo);
        httpInfo.setProgressInfo(progressInfo);
        httpInfo.setResponseInfo(stringInfo);
        return httpInfo;
    }

    /**
     * 根据网址获取要下载的文件的长度
     * @param url
     * @return
     */
    public static long getContentLength(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = getOkHttpClient().newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? -1 : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void OnError(HttpListener httpListener, Throwable e) {
        if (e instanceof HttpException) {
            httpListener.onError((HttpException) e);
        } else {
            httpListener.onError(new HttpException(-1, e.getMessage()));
        }
    }


    /**
     * 网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有网络连接
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 关闭流
     * @param closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
