package me.ibore.http.listener;

import okhttp3.ResponseBody;

public abstract class StringListener extends AbsHttpListener<String> {

    @Override
    public String convert(ResponseBody responseBody) throws Exception {
        return responseBody.string();
    }

}
