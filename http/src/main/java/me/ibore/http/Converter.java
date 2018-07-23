package me.ibore.http;

import java.io.IOException;

import okhttp3.ResponseBody;

public interface Converter<T> {

    T convert(okhttp3.Response value) throws IOException;

    Converter<ResponseBody> DEFAULT = value -> value.body();

}