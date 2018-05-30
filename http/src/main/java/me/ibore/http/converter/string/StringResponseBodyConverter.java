package me.ibore.http.converter.string;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public final class StringResponseBodyConverter implements Converter<ResponseBody, String> {

    StringResponseBodyConverter() {

    }

    @Override
    public String convert(ResponseBody value) throws IOException {
        return value.string();
    }
}

