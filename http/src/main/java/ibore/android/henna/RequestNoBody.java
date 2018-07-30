package ibore.android.henna;

public class RequestNoBody<T> extends Request<T, RequestNoBody<T>> {

    private String appendUrl = "";

    public RequestNoBody(Henna henna) {
        super(henna);
    }

    public RequestNoBody<T> appendUrl(String appendUrl) {
        this.appendUrl = appendUrl;
        return this;
    }

    @Override
    protected okhttp3.Request generateRequest() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        String url = getUrl() + appendUrl;
        builder.method(getMethod(), null)
                .url(HttpUtils.createUrlFromParams(url = url + appendUrl, getParams().urlParamsMap))
                .tag(getTag())
                .headers(HttpUtils.appendHeaders(getHeaders()));
        return builder.build();
    }
}
