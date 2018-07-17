package me.ibore.http.converter;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
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
    public File convert(ResponseBody value) throws IOException {
        File tempFile = new File(filePath, File.separator + System.currentTimeMillis());
        Okio.buffer(Okio.sink(tempFile)).writeAll(Okio.buffer(Okio.source(value.byteStream())));
        return tempFile;
    }
}
