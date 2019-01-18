package me.ibore.henna;

public final class RequestNoBody<T> extends Request<T, RequestNoBody<T>> {

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

    /**
     * 拼接参数，生成Request
     * @return okhttp3.Request
     */
    @Override
    protected okhttp3.Request generateRequest() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        String url = getUrl() + appendUrl;
        builder.method(getMethod(), null)
                .url(HennaUtils.generateUrlParams(url, getParams().urlParamsMap))
                .tag(getTag())
                .headers(HennaUtils.generateHeaders(getHeaders()));
        return builder.build();
    }
}
