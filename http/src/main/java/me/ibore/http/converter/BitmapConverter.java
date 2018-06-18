package me.ibore.http.converter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class BitmapConverter implements Converter<ResponseBody, Bitmap> {

    @Override
    public Bitmap convert(ResponseBody value) throws IOException {
        return BitmapFactory.decodeByteArray(value.bytes(), 0, value.bytes().length);
    }

}
