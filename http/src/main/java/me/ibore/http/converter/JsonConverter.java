package me.ibore.http.converter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.ResponseBody;

public class JsonConverter<T> implements Converter<T> {

    private JsonConverter() {
    }

    public static JsonConverter create() {
        return new JsonConverter();
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        return null;
    }

}
