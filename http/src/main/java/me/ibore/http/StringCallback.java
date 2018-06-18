package me.ibore.http;

import java.io.IOException;

import me.ibore.http.request.Request;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class StringCallback implements Callback<String>, Converter<RequestBody, String> {

    @Override
    public void onStart(Request<String, ? extends Request> request) {

    }

    @Override
    public String convert(RequestBody value) throws IOException {
        return null;
    }
}
