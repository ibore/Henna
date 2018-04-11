package me.ibore.http;

/**
 * Created by Administrator on 2018/2/7.
 */

public class StringInfo {

    private String url;
    private String data;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StringInfo{" +
                "url='" + url + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
