package me.ibore.http.request;

import me.ibore.http.utils.HttpUtils;

public class NoBodyRequest<T> extends Request<T, NoBodyRequest<T>> {

    private String appendUrl = "";

    public NoBodyRequest appendUrl(String appendUrl) {
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
