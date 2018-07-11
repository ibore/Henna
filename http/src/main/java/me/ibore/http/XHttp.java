package me.ibore.http;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import me.ibore.http.cookie.CookieStore;
import me.ibore.http.request.GetRequest;
import me.ibore.http.request.PostRequest;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:00
 * website: ibore.me
 */

public class XHttp {

    private OkHttpClient okHttpClient;
    private int timeout;
    private int refreshTime;
    private int maxRetry;
    private Cache cache;
    private CookieStore cookieStore;
    private List<Interceptor> interceptors;
    private List<Interceptor> networkInterceptors;
    private SSLSocketFactory sslSocketFactory;
    private LinkedHashMap<String, String> headers;
    private LinkedHashMap<String, String> params;

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

    public int timeout() {
        return timeout;
    }

    public int refreshTime() {
        return refreshTime;
    }

    public int maxRetry() {
        return maxRetry;
    }

    public Cache cache() {
        return cache;
    }

    public CookieStore cookieStore() {
        return cookieStore;
    }

    public List<Interceptor> interceptors() {
        return networkInterceptors;
    }

    public List<Interceptor> networkInterceptors() {
        return interceptors;
    }

    public LinkedHashMap<String, String> headers() {
        return headers;
    }

    public LinkedHashMap<String, String> params() {
        return params;
    }

    public final static Handler mDelivery = new Handler(Looper.getMainLooper());

    private XHttp(OkHttpClient okHttpClient, int timeout, int refreshTime, int maxRetry, Cache cache, CookieStore cookieStore,
                   List<Interceptor> interceptors, List<Interceptor> networkInterceptors,
                   SSLSocketFactory sslSocketFactory, LinkedHashMap<String, String> headers,
                   LinkedHashMap<String, String> params) {
        this.okHttpClient = okHttpClient;
        this.timeout = timeout;
        this.refreshTime = refreshTime;
        this.maxRetry = maxRetry;
        this.cache = cache;
        this.cookieStore = cookieStore;
        this.interceptors = interceptors;
        this.networkInterceptors = networkInterceptors;
        this.sslSocketFactory = sslSocketFactory;
        this.headers = headers;
        this.params = params;
    }

    public GetRequest get(String url) {
        return new GetRequest(this).url(url).method("GET");
    }

    public PostRequest post(String url) {
        return new PostRequest(this).url(url).method("POST");
    }

    public static Handler getDelivery() {
        return mDelivery;
    }

    public static void runOnUiThread(Runnable runnable) {
        mDelivery.post(runnable);
    }

    public void cancelTag(Object tag) {
        for (Call call : okHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void cancelAll() {
        for (Call call : okHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : okHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    public static class Builder {

        private int timeout;
        private int refreshTime;
        private int maxRetry;
        private Cache cache;
        private CookieStore cookieStore;
        private List<Interceptor> interceptors;
        private List<Interceptor> networkInterceptors;
        private SSLSocketFactory sslSocketFactory;
        private LinkedHashMap<String, String> headers;
        private LinkedHashMap<String, String> params;

        public Builder() {
            this.timeout = 60000;
            this.refreshTime = 300;
            this.maxRetry = 0;
            interceptors = new ArrayList<>();
            networkInterceptors = new ArrayList<>();
            headers = new LinkedHashMap<>();
            params = new LinkedHashMap<>();
        }

        public Builder(XHttp http) {
            this.timeout = http.timeout();
            this.refreshTime = http.refreshTime();
            this.maxRetry = http.maxRetry();
            this.cache = http.cache();
            this.cookieStore = http.cookieStore();
            this.interceptors = http.interceptors();
            this.networkInterceptors = http.networkInterceptors();
            this.headers = http.headers();
            this.params = http.params();
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        public Builder refreshTime(int refreshTime) {
            this.refreshTime = refreshTime;
            return this;
        }
        public Builder maxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
            return this;
        }
        public Builder cookieJar(CookieStore cookieStore) {
            this.cookieStore = cookieStore;
            return this;
        }
        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }
        public Builder addNetworkInterceptor(Interceptor interceptor) {
            networkInterceptors.add(interceptor);
            return this;
        }
        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }
        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }
        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }
        public Builder param(String key, String value) {
            params.put(key, value);
            return this;
        }
        public XHttp builder() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS);
            if (null != cache) builder.cache(cache);
            if (null != sslSocketFactory) builder.sslSocketFactory(sslSocketFactory);
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
            for (Interceptor networkInterceptor : networkInterceptors) {
                builder.addNetworkInterceptor(networkInterceptor);
            }
            return new XHttp(builder.build(), timeout, refreshTime, maxRetry, cache, cookieStore,
                    interceptors, networkInterceptors, sslSocketFactory, headers, params);
        }
    }


}
