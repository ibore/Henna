package me.ibore.http.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ibore.http.Henna;
import me.ibore.http.converter.Converter;
import me.ibore.http.exception.HttpException;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.progress.ProgressResponseBody;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class Request<R extends Request> {

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    public static final MediaType MEDIA_TYPE_FORM = MediaType.parse("multipart/form-data");

    protected Henna henna;
    protected String url;
    protected String method;
    protected OkHttpClient client;
    protected int maxRetry;
    protected int refreshTime;
    protected LinkedHashMap<String, List<String>> headers;
    protected LinkedHashMap<String, List<String>> urlParams;
    protected Object tag;
    protected CacheControl cacheControl;
    protected Converter converter;
    private boolean isProgress;


    public Request(Henna henna) {
        this.henna = henna;
        this.client = henna.okHttpClient();
        this.maxRetry = henna.maxRetry();
        this.refreshTime = henna.refreshTime();
        headers = new LinkedHashMap<>();
        urlParams = new LinkedHashMap<>();
        for (String key: henna.headers().keySet()) {
            List<String> values = new ArrayList<>();
            values.add(henna.headers().get(key));
            headers.put(key, values);
        }
        for (String key: henna.params().keySet()) {
            List<String> values = new ArrayList<>();
            values.add(henna.params().get(key));
            urlParams.put(key, values);
        }
    }

    public R method(String method) {
        this.method = method;
        return (R) this;
    }

    public R url(String url) {
        this.url = url;
        return (R) this;
    }

    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    public void maxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public void refreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public R cacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return (R) this;
    }

    public R header(String key, String value, boolean... isReplace) {
        if (key != null && value != null) {
            List<String> urlValues = headers.get(key);
            if (urlValues == null) {
                urlValues = new ArrayList<>();
                headers.put(key, urlValues);
            }
            if (isReplace.length > 0) {
                if (isReplace[0]) urlValues.clear();
            }
            urlValues.add(value);
        }
        return (R) this;
    }

    public R header(String key, List<String> values, boolean... isReplace) {
        if (key != null && values != null) {
            List<String> urlValues = headers.get(key);
            if (urlValues == null) {
                urlValues = new ArrayList<>();
                headers.put(key, urlValues);
            }
            if (isReplace.length > 0) {
                if (isReplace[0]) urlValues.clear();
            }
            urlValues.addAll(values);
        }
        return (R) this;
    }

    public R header(Map<String, String> header, boolean... isReplace) {
        if (header != null) {
            for (String key : header.keySet()) {
                List<String> urlValues = headers.get(key);
                if (urlValues == null) {
                    urlValues = new ArrayList<>();
                    headers.put(key, urlValues);
                }
                if (isReplace.length > 0) {
                    if (isReplace[0]) urlValues.clear();
                }
                urlValues.add(header.get(key));
            }
        }
        return (R) this;
    }

    public R headers(Map<String, List<String>> headers, boolean... isReplace) {
        if (headers != null) {
            for (String key : headers.keySet()) {
                List<String> urlValues = this.headers.get(key);
                if (urlValues == null) {
                    urlValues = new ArrayList<>();
                    this.headers.put(key, urlValues);
                }
                if (isReplace.length > 0) {
                    if (isReplace[0]) urlValues.clear();
                }
                urlValues.addAll(headers.get(key));
            }
        }
        return (R) this;
    }

    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public List<String> headers(String key) {
        return headers.get(key);
    }

    public R param(String key, int value, boolean... isReplace) {
        return param(key, String.valueOf(value), isReplace);
    }

    public R param(String key, long value, boolean... isReplace) {
        return param(key, String.valueOf(value), isReplace);
    }

    public R param(String key, double value, boolean... isReplace) {
        return param(key, String.valueOf(value), isReplace);
    }

    public R param(String key, float value, boolean... isReplace) {
        return param(key, String.valueOf(value), isReplace);
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

    public R param(Map<String, String> params, boolean... isReplace) {
        if (params != null) {
            for (String key : params.keySet()) {
                List<String> urlValues = urlParams.get(key);
                if (urlValues == null) {
                    urlValues = new ArrayList<>();
                    urlParams.put(key, urlValues);
                }
                if (isReplace.length > 0) {
                    if (isReplace[0]) urlValues.clear();
                }
                urlValues.add(params.get(key));
            }
        }
        return (R) this;
    }

    public R param(String key, List<String> values, boolean... isReplace) {
        if (key != null && values != null) {
            List<String> urlValues = urlParams.get(key);
            if (urlValues == null) {
                urlValues = new ArrayList<>();
                urlParams.put(key, urlValues);
            }
            if (isReplace.length > 0) {
                if (isReplace[0]) urlValues.clear();
            }
            urlValues.addAll(values);
        }
        return (R) this;
    }

    public R params(Map<String, List<String>> params, boolean... isReplace) {
        if (params != null) {
            for (String key : params.keySet()) {
                List<String> urlValues = urlParams.get(key);
                if (urlValues == null) {
                    urlValues = new ArrayList<>();
                    urlParams.put(key, urlValues);
                }
                if (isReplace.length > 0) {
                    if (isReplace[0]) urlValues.clear();
                }
                urlValues.addAll(params.get(key));
            }
        }
        return (R) this;
    }

    public R removeParam(String key) {
        urlParams.remove(key);
        return (R) this;
    }

    public Map<String, List<String>> urlParams() {
        return urlParams;
    }

    public List<String> urlParams(String key) {
        return urlParams.get(key);
    }

    public R client(OkHttpClient client) {
        this.client = client;
        return (R) this;
    }

    public R progress(boolean isProgress) {
        this.isProgress = isProgress;
        return (R) this;
    }

    public Response execute() throws IOException {
        okhttp3.Request.Builder builder = generateRequest(null);
        return client.newCall(builder.build()).execute();
    }

    public void enqueue(HennaListener listener) {
        okhttp3.Request.Builder builder = generateRequest(listener);
        client.newCall(builder.build()).enqueue(new okhttp3.Callback() {
            int retryCount = 0;
            @Override
            public void onFailure(Call call, IOException e) {
                if (retryCount < maxRetry) {
                    client.newCall(builder.build()).enqueue(this);
                    retryCount++;
                } else {
                    henna.runOnUiThread(() -> listener.onError(new HttpException(-1, e.getMessage())));
                }
            }
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        if (isProgress) {
                            response = response.newBuilder().body(new ProgressResponseBody(
                                    henna.getDelivery(), response.body(), listener, refreshTime)).build();
                        }
                        if (null != converter) {
                            Object object = converter.convert(response.body());
                            henna.runOnUiThread(() -> listener.onSuccess(object));
                        } else {
                            Response finalResponse = response;
                            henna.runOnUiThread(() -> listener.onSuccess(finalResponse.body()));
                        }
                    } catch (Exception e)  {
                        henna.runOnUiThread(() -> listener.onError(new HttpException(-1, e.getMessage())));
                    }
                } else {
                    Response finalResponse = response;
                    henna.runOnUiThread(() -> listener.onError(new HttpException(finalResponse.code(), finalResponse.message())));
                }

            }
        });
    }

    Headers generateHeaders() {
        Headers.Builder builder = new Headers.Builder();
        for (String key : headers.keySet()) {
            for (String value : headers.get(key)){
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    protected abstract okhttp3.Request.Builder generateRequest(HennaListener listener);


}
