package me.ibore.http.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import me.ibore.http.XHttp;
import me.ibore.http.listener.AbsHttpListener;
import okhttp3.CacheControl;

public class GetRequest extends Request<GetRequest> {

    private String appendUrl = "";

    public GetRequest(String url, XHttp xHttp) {
        super(url, xHttp);
    }

    @Override
    protected okhttp3.Request generateRequest(AbsHttpListener listener) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .get()
                .url(generateUrlParams())
                .tag(tag)
                .cacheControl(null == cacheControl ? new CacheControl.Builder().noCache().build() : cacheControl)
                .headers(generateHeaders())
                .build();
        return request;
    }

    protected String generateUrlParams() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            sb.append(appendUrl);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, List<String>> entry : urlParams.entrySet()) {
                List<String> urlValues = entry.getValue();
                for (String value : urlValues) {
                    //对参数进行 utf-8 编码,防止头信息传中文
                    String urlValue = URLEncoder.encode(value, "UTF-8");
                    sb.append(entry.getKey()).append("=").append(urlValue).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {

        }
        return url;
    }

    public GetRequest url(String appendUrl) {
        this.appendUrl = appendUrl;
        return this;
    }
}
