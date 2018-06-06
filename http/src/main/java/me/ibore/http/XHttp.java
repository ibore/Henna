package me.ibore.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ibore.http.converter.FileConverterFactory;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.ibore.http.Utils.CHECKNULL;
import static me.ibore.http.Utils.createDownloadInfo;
import static me.ibore.http.Utils.createStringInfo;


/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:00
 * website: ibore.me
 */

public class XHttp {

    public static int RETRY_COUNT = 3;

    public static int REFRESH_TIME = 300;

    public static int TIME_OUT = 10000;

    private static Context mContext;

    private static OkHttpClient mOkHttpClient;

    public static final Handler Handler = new Handler(Looper.getMainLooper());

    /**
     * 初始化
     * @param okHttpClient 自定义的OKHttpClient
     * @param RETRY_COUNT 重试次数(默认3次)
     * @param REFRESH_TIME 进度刷新时间(默认300)
     */
    public static void init(Context mContext, OkHttpClient okHttpClient, int RETRY_COUNT, int REFRESH_TIME) {
        XHttp.mContext = mContext;
        XHttp.mOkHttpClient = okHttpClient;
        XHttp.RETRY_COUNT = RETRY_COUNT;
        XHttp.REFRESH_TIME = REFRESH_TIME;
        XHttp.TIME_OUT = okHttpClient.connectTimeoutMillis();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 获取OkHttpClient
     * @return OkHttpClient
     */
    public static OkHttpClient getOkHttpClient() {
        if (null == mOkHttpClient) {
            HttpInterceptor httpInterceptor = new HttpInterceptor("HTTP");
            httpInterceptor.setPrintLevel(HttpInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder().addInterceptor(httpInterceptor).build();
        }
        return mOkHttpClient;
    }

    /**
     * 创建Retrofit
     * @param baseUrl 公共网址
     * @return Retrofit
     */
    public static Retrofit createGsonRetrofit(String baseUrl) {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    /**
     * 创建Retrofit
     * @param baseUrl 公共网址
     * @return Retrofit
     */
    public static Retrofit createFileRetrofit(String baseUrl) {
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(FileConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    /**
     * 下载文件（带缓存）支持断点下载
     * @param url
     * @param fileDirs
     * @param observer
     */
    public static void download(String url, File fileDirs, DownloadObserver observer) {
        CHECKNULL(getOkHttpClient());
        Observable.just(url)
                .flatMap(s -> Observable.just(createDownloadInfo(s, fileDirs)))
                .flatMap(httpInfo -> Observable.create(new DownloadSubscribe(httpInfo)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * 下载数据
     * @param url 网址
     * @param observer 回调
     */
    public static void download(String url, StringObserver observer) {
        CHECKNULL(getOkHttpClient());
        Observable.just(url)
                .flatMap(s -> Observable.just(createStringInfo(s)))
                .flatMap(httpInfo -> Observable.create(new StringSubscribe(httpInfo)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * 取消单个请求
     * @param tag
     */
    public static void cancel(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 取消全部请求
     */
    public static void cancelAll() {
        getOkHttpClient().dispatcher().cancelAll();
    }





}
