package me.ibore.http.converter.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public final class BitmapResponseBodyConverter implements Converter<ResponseBody, Bitmap> {

    BitmapResponseBodyConverter() {

    }

    @Override
    public Bitmap convert(ResponseBody value) throws IOException {
        byte[] bytes = value.bytes();
        return BitmapFactory.decodeByteArray(value.bytes(), 0, bytes.length);
    }
}

