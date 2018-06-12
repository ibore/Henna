package me.ibore.http.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.Okio;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class FileConverterFactory extends Converter.Factory {

    private File filePath;

    private FileConverterFactory(File filePath) {
        if (filePath.isDirectory()) {
            this.filePath = filePath;
        } else {
            throw new NullPointerException("this file not is directory");
        }
    }

    public static FileConverterFactory create(File filePath){
        return new FileConverterFactory(filePath);

    }

    @Override
    public Converter<ResponseBody, File> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return value -> {
            File tempFile = new File(filePath, File.separator + System.currentTimeMillis());
            Okio.buffer(Okio.sink(tempFile)).writeAll(Okio.buffer(Okio.source(value.byteStream())));
            return tempFile;
        };

    }

}
