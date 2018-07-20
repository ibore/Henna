package me.ibore.http.request;

import java.util.List;
import java.util.Map;

import me.ibore.http.Henna;
import me.ibore.http.HttpHeaders;
import me.ibore.http.HttpParams;
import me.ibore.http.Response;
import me.ibore.http.call.Call;
import me.ibore.http.call.OkHttpCall;
import me.ibore.http.converter.Converter;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.progress.ProgressRequestBody;
import me.ibore.http.utils.HttpUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public abstract class Request<T, R extends Request> {

    protected Henna henna;
    protected OkHttpClient client;
    protected String method;
    protected String url;
    protected Object tag;
    protected int maxRetry;
    protected int refreshTime;
    protected HttpHeaders headers;
    protected HttpParams params;

    protected Call<T> call;
    protected Converter<T> converter;
    protected ProgressListener uploadListener;
    protected ProgressListener downloadListener;
    protected boolean thread;
    private okhttp3.Request rawRequest;

    @SuppressWarnings("unchecked")
    public R client(OkHttpClient client) {
        this.client = client;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R method(String method) {
        this.method = method;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R url(String url) {
        this.url = url;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R maxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R refreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllHeaders() {
        headers.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(HttpParams params) {
        this.params.put(params);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(Map<String, String> params, boolean... isReplace) {
        this.params.put(params, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, int value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, float value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, double value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, long value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, char value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, boolean value, boolean... isReplace) {
        params.put(key, value, isReplace);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R addUrlParams(String key, List<String> values) {
        params.putUrlParams(key, values);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeParam(String key) {
        params.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeAllParams() {
        params.clear();
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R call(Call<T> call) {
        this.call = call;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R converter(Converter<T> converter) {
        this.converter = converter;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R upload(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R download(ProgressListener downloadListener) {
        this.downloadListener = downloadListener;
        return (R) this;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Object getTag() {
        return tag;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpParams getParams() {
        return params;
    }

    public Converter<T> getConverter() {
        return converter;
    }

    public ProgressListener getUploadListener() {
        return uploadListener;
    }

    public ProgressListener getDownloadListener() {
        return downloadListener;
    }

    public boolean isThread() {
        return thread;
    }

    public Response<T> execute() throws Exception {
        if (null == call) {
            return new OkHttpCall<T>(this).execute();
        } else {
            return call.execute();
        }
    }

    public void enqueue(HennaListener<T> listener) {
        if (null == call) {
            new OkHttpCall<T>(this).enqueue(listener);
        } else {
            call.enqueue(listener);
        }
    }

    public okhttp3.Call getRawCall() {
        rawRequest = generateRequest();
        return client.newCall(rawRequest);
    }

    protected abstract okhttp3.Request generateRequest();


/*   public Response<T> execute() throws Exception {
        return new OkHttpCall<T>(this).execute();
    }

    public void enqueue(HennaListener<T> listener) {
        if (null == listener) throw new NullPointerException("HennaListener can not be null");
        henna.runOnUiThread(listener::onStart);
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
                    henna.runOnUiThread(listener::onFinish);
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response rawResponse) throws IOException {
                if (rawResponse.isSuccessful()) {
                    try {
                        if (null != downloadListener) {
                            rawResponse = rawResponse.newBuilder().body(ProgressResponseBody.create(rawResponse.body(), downloadListener, refreshTime)).build();
                        }
                        if (null == converter)
                            throw new NullPointerException("converter can not be null");
                        T object = converter.convert(rawResponse.body());
                        henna.runOnUiThread(() -> listener.onResponse(call, object));
                    } catch (Exception e) {
                        henna.runOnUiThread(() -> listener.onError(new HttpException(-1, e.getMessage())));
                    }
                } else {
                    Response finalResponse = response;
                    henna.runOnUiThread(() -> listener.onError(new HttpException(finalResponse.code(), finalResponse.message())));
                }
                henna.runOnUiThread(listener::onFinish);
            }

        });
    }

    protected abstract okhttp3.Request.Builder generateRequest(HennaListener listener);

    public Observable<T> observable() {
        return observable(null);
    }

    public Observable<T> observable(ProgressListener listener) {
        return Observable.create((ObservableOnSubscribe<ResponseBody>) emitter -> {
            try {
                okhttp3.Request.Builder builder = generateRequest(null);
                Response response = client.newCall(builder.build()).execute();
                if (response.isSuccessful()) {
                    if (isProgress) {
                        if (null == listener)
                            throw new NullPointerException("ProgressListener can not be null");
                        response = response.newBuilder().body(ProgressResponseBody.create(
                                henna.getDelivery(), response.body(), listener, refreshTime)).build();
                    }
                    emitter.onNext(response.body());
                } else {
                    emitter.onError(HttpException.newInstance(response.code()));
                }
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
            emitter.setCancellable(new Cancellable() {
                @Override
                public void cancel() throws Exception {
                    henna.cancelTag(tag);
                }
            });
        }).map(responseBody -> {
            if (null == converter)
                throw new NullPointerException("converter can not be null");
            return converter.convert(responseBody);
        });
    }*/

}
