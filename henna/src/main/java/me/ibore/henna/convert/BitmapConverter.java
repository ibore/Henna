package me.ibore.henna.convert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import me.ibore.henna.Converter;
import me.ibore.henna.exception.ConvertException;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BitmapConverter implements Converter<Bitmap> {

    private BitmapConverter() {
    }

    public static BitmapConverter create() {
        return new BitmapConverter();
    }

    @Override
    public Bitmap convert(Response value) throws IOException, ConvertException {
        try {
            ResponseBody body = value.body();
            return BitmapFactory.decodeByteArray(body.bytes(), 0, body.bytes().length);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ConvertException("Convert Error", e);
        }
    }
}