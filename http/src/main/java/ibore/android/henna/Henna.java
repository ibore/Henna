package ibore.android.henna;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import ibore.android.henna.adapter.rxjava2.RxJava2CallAdapter;
import ibore.android.henna.cookie.CookieStore;
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

public class Henna {

    private OkHttpClient okHttpClient;
    private int timeout;
    private int refreshTime;
    private int maxRetry;
    private Cache cache;
    private CookieStore cookieStore;
    private List<Interceptor> interceptors;
    private List<Interceptor> networkInterceptors;
    private SSLSocketFactory sslSocketFactory;
    private HttpHeaders headers;
    private HttpParams params;
    private Converter converter;
    private CallAdapter<?, ?> callAdapter = new RxJava2CallAdapter<>();

    public OkHttpClient client() {
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

    public HttpHeaders headers() {
        return headers;
    }

    public HttpParams params() {
        return params;
    }

    public <T> Converter<T> converter() {
        return converter;
    }

    private Henna(OkHttpClient okHttpClient, int timeout, int refreshTime, int maxRetry, Cache cache, CookieStore cookieStore,
                  List<Interceptor> interceptors, List<Interceptor> networkInterceptors,
                  SSLSocketFactory sslSocketFactory, HttpHeaders headers,
                  HttpParams params, Converter converter) {
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
        this.converter = converter;
    }

    public <T> RequestNoBody<T> get(String url) {
        return new RequestNoBody<T>(this).url(url).method("GET");
    }

    public <T> RequestHasBody<T> post(String url) {
        return new RequestHasBody<T>(this).url(url).method("POST");
    }

    public <T> RequestHasBody<T> put(String url) {
        return new RequestHasBody<T>(this).url(url).method("PUT");
    }

    public <T> RequestNoBody<T> head(String url) {
        return new RequestNoBody<T>(this).url(url).method("HEAD");
    }

    public <T> RequestHasBody<T> delete(String url) {
        return new RequestHasBody<T>(this).url(url).method("DELETE");
    }

    public <T> RequestHasBody<T> options(String url) {
        return new RequestHasBody<T>(this).url(url).method("OPTIONS");
    }

    public <T> RequestHasBody<T> patch(String url) {
        return new RequestHasBody<T>(this).url(url).method("PATCH");
    }

    public <T> RequestNoBody<T> trace(String url) {
        return new RequestNoBody<T>(this).url(url).method("TRACE");
    }

    public void cancelTag(Object tag) {
        for (Call call : client().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void cancelAll() {
        for (Call call : client().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : client().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    public CallAdapter callAdapter() {
        return callAdapter;
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
        private HttpHeaders headers;
        private HttpParams params;
        private Converter converter;

        public Builder() {
            this.timeout = 10000;
            this.refreshTime = 300;
            this.maxRetry = 0;
            interceptors = new ArrayList<>();
            networkInterceptors = new ArrayList<>();
            headers = new HttpHeaders();
            params = new HttpParams();
        }

        public Builder(Henna henna) {
            this.timeout = henna.timeout();
            this.refreshTime = henna.refreshTime();
            this.maxRetry = henna.maxRetry();
            this.cache = henna.cache();
            this.cookieStore = henna.cookieStore();
            this.interceptors = henna.interceptors();
            this.networkInterceptors = henna.networkInterceptors();
            this.headers = henna.headers();
            this.params = henna.params();
            this.converter = henna.converter();
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

        public Builder converter(Converter converter) {
            this.converter = converter;
            return this;
        }

        public Henna builder() {
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
            if (null == converter) {
                converter = Converter.DEFAULT;
            }
            return new Henna(builder.build(), timeout, refreshTime, maxRetry, cache, cookieStore,
                    interceptors, networkInterceptors, sslSocketFactory, headers, params, converter);
        }
    }


}
