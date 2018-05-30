package me.ibore.http.converter.string;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class StringConverterFactory extends Converter.Factory {

    private StringConverterFactory() {

    }

    public static StringConverterFactory create(){
        return new StringConverterFactory();
    }

    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return StringRequestBodyConverter.INSTANCE;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new StringResponseBodyConverter();
    }
}