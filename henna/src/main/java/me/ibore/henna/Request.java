package me.ibore.henna;

import java.util.List;
import java.util.Map;

import me.ibore.henna.progress.ProgressListener;
import okhttp3.OkHttpClient;

public abstract class Request<T, R extends Request> {

    private OkHttpClient client;
    private String baseUrl;
    private String method;
    private String url;
    private Object tag;
    private int maxRetry;
    private int refreshTime;
    private HttpHeaders headers;
    private HttpParams params;

    private CallAdapter<T, ?> callAdapter;
    private Converter<T> converter;
    private ProgressListener downloadListener;
    private boolean uiThread = true;

    private Call<T> call;

    public Request() {
        headers = new HttpHeaders();
        params = new HttpParams();
    }

    @SuppressWarnings("unchecked")
    public Request(Henna henna) {
        client = henna.client();
        baseUrl = henna.baseUrl();
        maxRetry = henna.maxRetry();
        refreshTime = henna.refreshTime();
        headers = henna.headers();
        if (null == headers) headers = new HttpHeaders();
        params = henna.params();
        if (null == params) params = new HttpParams();
        converter = henna.converter();
        callAdapter = henna.callAdapter();
    }

    @SuppressWarnings("unchecked")
    public R client(OkHttpClient client) {
        this.client = client;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R method(String method) {
        if ((this instanceof RequestHasBody && HennaUtils.hasBody(method)) ||
                (this instanceof RequestNoBody && !HennaUtils.hasBody(method))) {
            this.method = method;
        } else {
            throw new RuntimeException("Wrong request method");
        }
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R url(String url) {
        HennaUtils.checkNotNull(url, "url can't be null");
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
    public R params(String key, List<String> values, boolean... isReplace) {
        params.put(key, values, isReplace);
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
    public R converter(Converter<T> converter) {
        this.converter = converter;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R adapter(CallAdapter<T, ?> callAdapter) {
        this.callAdapter = callAdapter;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R download(ProgressListener downloadListener) {
        this.downloadListener = downloadListener;
        return (R) this;
    }
    @SuppressWarnings("unchecked")
    public R uiThread(boolean uiThread) {
        this.uiThread = uiThread;
        return (R) this;
    }

    public OkHttpClient getClient() {
        return HennaUtils.checkNotNull(client, "OkHttpClient can not be null");
    }

    public String baseUrl() {
        return baseUrl;
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
        return HennaUtils.checkNotNull(converter, "converter can not be null");
    }

    public ProgressListener getDownloadListener() {
        return downloadListener;
    }

    public boolean isUIThread() {
        return uiThread;
    }

    public Response<T> execute() throws Exception {
        return getCall().execute();
    }

    public void enqueue(HennaListener<T> listener) {
        getCall().enqueue(listener);
    }

    public Call<T> getCall() {
        if (null == call) call = new HennaCall<>(this);
        return call;
    }

    protected abstract okhttp3.Request generateRequest();

    public <E> E adapter() {
        return adapter(false);
    }

    @SuppressWarnings("unchecked")
    public <E> E adapter(boolean isAsync) {
        HennaUtils.checkNotNull(callAdapter, "CallAdapter cannot be null");
        return (E) callAdapter.adapter(getCall(), isAsync);
    }

    public void cancel() {
        getCall().cancel();
    }
}
