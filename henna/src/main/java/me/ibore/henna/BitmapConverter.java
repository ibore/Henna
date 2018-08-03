package me.ibore.henna;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class BitmapConverter implements Converter<Bitmap> {

    private BitmapConverter() {
    }

    public static BitmapConverter create() {
        return new BitmapConverter();
    }

    @Override
    public Bitmap convert(Response value) throws IOException {
        ResponseBody body = value.body();
        return BitmapFactory.decodeByteArray(body.bytes(), 0, body.bytes().length);
    }
}