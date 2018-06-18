package me.ibore.http.request;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ibore.http.FileWrapper;
import me.ibore.http.HttpListener;
import me.ibore.http.XHttp;
import me.ibore.http.exception.HttpException;
import me.ibore.http.progress.ProgressListener;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class Request<T, R extends Request> {

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    protected XHttp http;
    protected String url;
    protected OkHttpClient client;
    protected LinkedHashMap<String, List<String>> urlParams;
    protected LinkedHashMap<String, List<FileWrapper>> fileParams;
    protected Headers.Builder headersBuilder;
    protected Object tag;
    protected CacheControl cacheControl;
    private ProgressListener downloadListener;

    public Request(String url, XHttp http) {
        this.url = url;
        this.http = http;
        headersBuilder = new Headers.Builder();
        urlParams = new LinkedHashMap<>();
        fileParams = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : http.headers().entrySet()) {
            headersBuilder.add(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : http.params().entrySet()) {
            List<String> values = new ArrayList<>();
            values.add(entry.getValue());
            urlParams.put(entry.getKey(), values);
        }
    }

    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    public R cacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return (R) this;
    }

    public R header(String key, String value, boolean... isReplace) {
        if (isReplace.length > 0) {
            if (isReplace[0]) {
                headersBuilder.set(key, value);
                return (R) this;
            }
        }
        headersBuilder.add(key, value);
        return (R) this;
    }

    public R removeHeader(String key) {
        headersBuilder.removeAll(key);
        return (R) this;
    }

    public R param(String key, String value, boolean... isReplace) {
        if (key != null && value != null) {
            List<String> urlValues = urlParams.get(key);
            if (urlValues == null) {
                urlValues = new ArrayList<>();
                urlParams.put(key, urlValues);
            }
            if (isReplace.length > 0) {
                if (isReplace[0]) urlValues.clear();
            }
            urlValues.add(value);
        }
        return (R) this;
    }

    public R removeParam(String key) {
        urlParams.remove(key);
        return (R) this;
    }

    public R client(OkHttpClient client) {
        this.client = client;
        return (R) this;
    }

    public R download(ProgressListener downloadListener) {
        this.downloadListener = downloadListener;
        return (R) this;
    }

    public Response execute() throws IOException {
        okhttp3.Request request = generateRequest(null);
        return http.okHttpClient().newCall(request).execute();
    }

    public void enqueue(HttpListener<File> listener) {
        okhttp3.Request request = generateRequest(listener);
        http.okHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(new HttpException(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


            }
        });

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof File) {

        }
    }

    protected Headers generateHeaders() {
        return headersBuilder.build();
    }

    protected abstract okhttp3.Request generateRequest(HttpListener listener);

}
