package me.ibore.henna;

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
        builder.method(method, null)
                .url(HttpUtils.createUrlFromParams(url = url + appendUrl, params.urlParamsMap))
                .tag(tag)
                .headers(HttpUtils.appendHeaders(headers));
        return builder.build();
    }
}
