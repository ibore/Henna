package me.ibore.henna.convert;

import java.io.IOException;

import me.ibore.henna.Converter;
import me.ibore.henna.exception.ConvertException;
import okhttp3.Response;

public class StringConverter implements Converter<String> {

    private StringConverter() {

    }

    public static StringConverter create(){
        return new StringConverter();
    }


    @Override
    public String convert(Response value) throws IOException, ConvertException {
        try {
            return value.body().string();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ConvertException("Convert Error", e);
        }
    }
}