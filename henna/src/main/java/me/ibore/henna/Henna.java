package me.ibore.henna;

import java.io.File;

import me.ibore.henna.convert.FileConverter;
import me.ibore.henna.download.DownloadListener;
import me.ibore.henna.progress.ProgressListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:00
 * website: ibore.me
 */

public final class Henna {

    private OkHttpClient client;
    private int refreshTime;
    private int maxRetry;
    private HttpHeaders headers;
    private HttpParams params;
    private Converter converter;
    private CallAdapter<?, ?> callAdapter;

    public OkHttpClient client() {
        return client;
    }

    public int refreshTime() {
        return refreshTime;
    }

    public int maxRetry() {
        return maxRetry;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public HttpParams params() {
        return params;
    }

    @SuppressWarnings("unchecked")
    public <T> Converter<T> converter() {
        return converter;
    }

    public CallAdapter callAdapter() {
        return callAdapter;
    }

    private Henna(OkHttpClient client, int refreshTime, int maxRetry, HttpHeaders headers,
                  HttpParams params, Converter converter, CallAdapter callAdapter) {
        this.client = client;
        this.refreshTime = refreshTime;
        this.maxRetry = maxRetry;
        this.headers = headers;
        this.params = params;
        this.converter = converter;
        this.callAdapter = callAdapter;
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

    public void download(String fileDir, String url, DownloadListener downloadListener) {
        String fileName = HennaUtils.getUrlFileName(url);
        File file = new File(fileDir, fileName);
        Long range = 0L;
        if (file.exists()) {
            range = file.length();
        }
        new RequestNoBody<File>(this).url(url)
                .method("GET")
                .headers("RANGE", "bytes=" + range + "-")
                .converter(FileConverter.create(fileDir))
                .download(downloadListener)
                .enqueue(downloadListener);
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

    public static class Builder {

        private OkHttpClient client;
        private int refreshTime;
        private int maxRetry;
        private HttpHeaders headers;
        private HttpParams params;
        private Converter converter;
        private CallAdapter callAdapter;

        public Builder() {
            this.refreshTime = 300;
            this.maxRetry = 0;
            headers = new HttpHeaders();
            params = new HttpParams();
        }

        public Builder(Henna henna) {
            this.client = henna.client();
            this.refreshTime = henna.refreshTime();
            this.maxRetry = henna.maxRetry();
            this.headers = henna.headers();
            this.params = henna.params();
            this.converter = henna.converter();
        }

        public Builder client(OkHttpClient client) {
            this.client = HennaUtils.checkNotNull(client, "OkHttpClient can not be null");
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

        public Builder callAdapter(CallAdapter callAdapter) {
            this.callAdapter = callAdapter;
            return this;
        }

        public Henna builder() {
            HennaUtils.checkNotNull(client, "OkHttpClient can not be null");
            return new Henna(client, refreshTime, maxRetry, headers, params, converter, callAdapter);
        }
    }


}
