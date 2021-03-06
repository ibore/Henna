package me.ibore.henna;

public class RequestNoBody<T> extends Request<T, RequestNoBody<T>> {

    private String appendUrl = "";

    public RequestNoBody() {
        super();
    }

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
                .url(HennaUtils.createUrlFromParams(url, getParams().urlParamsMap))
                .tag(getTag())
                .headers(HennaUtils.appendHeaders(getHeaders()));
        return builder.build();
    }
}
