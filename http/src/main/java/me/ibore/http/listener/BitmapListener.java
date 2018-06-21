package me.ibore.http.listener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.ResponseBody;

public abstract class BitmapListener extends AbsHttpListener<Bitmap> {

    @Override
    public Bitmap convert(ResponseBody responseBody) throws Exception {
        return BitmapFactory.decodeByteArray(responseBody.bytes(), 0, responseBody.bytes().length);
    }

}
