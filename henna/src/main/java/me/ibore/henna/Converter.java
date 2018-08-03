package me.ibore.henna;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;

public interface Converter<T> {

    T convert(okhttp3.Response value) throws IOException;

    Converter<ResponseBody> DEFAULT = new Converter<ResponseBody>() {
        @Override
        public ResponseBody convert(Response value) throws IOException {
            return value.body();
        }
    };

}