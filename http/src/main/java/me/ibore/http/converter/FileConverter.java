package me.ibore.http.converter;

import java.io.File;
import java.io.IOException;

import me.ibore.http.Converter;
import me.ibore.http.HttpUtils;
import okhttp3.Response;
import okio.Okio;

public class FileConverter implements Converter<File> {

    private File filePath;

    private FileConverter(File filePath) {
        if (filePath.isDirectory()) {
            this.filePath = filePath;
        } else {
            throw new NullPointerException("this file not is directory");
        }
    }

    public static FileConverter create(File filePath) {
        return new FileConverter(filePath);
    }

    @Override
    public File convert(Response value) throws IOException {
        /*HttpUtils.getNetFileName(value, value.header())*/
        File tempFile = new File(filePath, File.separator + System.currentTimeMillis());
        Okio.buffer(Okio.sink(tempFile)).writeAll(Okio.buffer(Okio.source(value.body().byteStream())));
        return tempFile;
    }
}
