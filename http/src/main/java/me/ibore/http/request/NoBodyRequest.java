package me.ibore.http.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import me.ibore.http.Henna;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.utils.HttpUtils;
import okhttp3.CacheControl;
import okhttp3.RequestBody;

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
                .url(HttpUtils.createUrlFromParams(url, params.urlParamsMap))
                .tag(tag)
                .headers(HttpUtils.appendHeaders(headers));
        return builder.build();
    }
}
