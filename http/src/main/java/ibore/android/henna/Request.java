package ibore.android.henna;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public abstract class Request<T, R extends Request> {

    private Henna henna;
    private OkHttpClient client;
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
    private boolean isUIThread = true;

    public Request(Henna henna) {
        this.henna = henna;
        client = henna.client();
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
        if ((this instanceof RequestHasBody && HttpUtils.hasBody(method)) ||
                (this instanceof RequestNoBody && !HttpUtils.hasBody(method))) {
            this.method = method;
        } else {
            throw new RuntimeException("Wrong request method");
        }
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

    public ProgressListener getDownloadListener() {
        return downloadListener;
    }

    public boolean isUIThread() {
        return isUIThread;
    }

    public Response<T> execute() throws Exception {
        return new OkHttpCall<>(this).execute();
    }

    public void enqueue(HennaListener<T> listener) {
        new OkHttpCall<>(this).enqueue(listener);
    }

    public okhttp3.Call getRawCall() {
        return client.newCall(generateRequest());
    }

    public Call<T> getCall() {
        return new OkHttpCall<>(this);
    }

    protected abstract okhttp3.Request generateRequest();

    public <E> E adapter() {
        return (E) callAdapter.adapter(getCall(), false);
    }

}
