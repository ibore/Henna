package me.ibore.http.request;

import me.ibore.http.Henna;

public class NoBodyBuilder<T> extends RequestBuilder<T, NoBodyBuilder> {

    private String appendUrl = "";

    public NoBodyBuilder(Henna henna) {
        super(henna);
    }

    public NoBodyBuilder appendUrl(String appendUrl) {
        this.appendUrl = appendUrl;
        return this;
    }

}

