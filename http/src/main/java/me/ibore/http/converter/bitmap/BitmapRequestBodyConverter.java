package me.ibore.http.converter.bitmap;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class BitmapRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    BitmapRequestBodyConverter() {

    }

    @Override
    public RequestBody convert(T value) throws IOException {
        return null;
    }
}
