package me.ibore.http.request;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ibore.http.Henna;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.progress.ProgressListener;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

public class RequestBuilder<T, R extends RequestBuilder> {

    private Henna henna;
    private String url;
    private String method;
    private OkHttpClient client;
    private int maxRetry;
    private int refreshTime;
    private LinkedHashMap<String, List<String>> headers;
    private LinkedHashMap<String, List<String>> urlParams;
    private Object tag;
    private CacheControl cacheControl;
    private Converter<T> converter;
    private ProgressListener uploadListener;
    private ProgressListener downloadListener;
    private boolean thread;

    public RequestBuilder() {
        headers = new LinkedHashMap<>();
        urlParams = new LinkedHashMap<>();
        maxRetry = 0;
        this.refreshTime = 300;
    }

    public RequestBuilder(Henna henna) {
        this.henna = henna;
        this.client = henna.okHttpClient();
        this.maxRetry = henna.maxRetry();
        this.refreshTime = henna.refreshTime();
        headers = new LinkedHashMap<>();
        urlParams = new LinkedHashMap<>();
        for (String key : henna.headers().keySet()) {
            List<String> values = new ArrayList<>();
            values.add(henna.headers().get(key));
            headers.put(key, values);
        }
        for (String key : henna.params().keySet()) {
            List<String> values = new ArrayList<>();
            values.add(henna.params().get(key));
            urlParams.put(key, values);
        }
        this.converter = henna.converter();
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

    public R upload(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
        return (R) this;
    }

    public R download(ProgressListener downloadListener) {
        this.downloadListener = downloadListener;
        return (R) this;
    }

    public R converter(Converter<T> converter) {
        this.converter = converter;
        return (R) this;
    }

    public R thread(boolean thread) {
        this.thread = thread;
        return (R) this;
    }

    public Request builder() {
        return new Request<T>(client, method, url, tag, headers, urlParams, maxRetry, refreshTime,
                cacheControl, converter, uploadListener, downloadListener) {
            @Override
            protected okhttp3.Request.Builder generateRequest(HennaListener listener) {
                return null;
            }

            @Override
            public okhttp3.Request request() {
                return null;
            }
        };
    }
}