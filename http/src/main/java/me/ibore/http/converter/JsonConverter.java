package me.ibore.http.converter;

import java.io.IOException;

import me.ibore.http.Converter;
import okhttp3.Response;

public class JsonConverter<T> implements Converter<T> {

    private JsonConverter() {

    }

    public static JsonConverter create() {
        return new JsonConverter();
    }

    @Override
    public T convert(Response value) throws IOException {
        return null;
    }
}
