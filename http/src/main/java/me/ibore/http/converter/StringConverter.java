package me.ibore.http.converter;

import java.io.IOException;

import okhttp3.ResponseBody;

public class StringConverter implements Converter<String> {

    private StringConverter() {

    }

    public static StringConverter create(){
        return new StringConverter();
    }

    @Override
    public String convert(ResponseBody value) throws IOException {
        return value.string();
    }
}