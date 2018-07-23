package me.ibore.http.converter;

import java.io.IOException;

import me.ibore.http.Converter;
import okhttp3.Response;

public class StringConverter implements Converter<String> {

    private StringConverter() {

    }

    public static StringConverter create(){
        return new StringConverter();
    }

    @Override
    public String convert(Response value) throws IOException {
        return value.body().string();
    }
}