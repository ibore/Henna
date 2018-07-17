package me.ibore.http.converter;

import java.io.IOException;

import okhttp3.ResponseBody;

public interface Converter<T> {

    T convert(ResponseBody value) throws IOException;

}
