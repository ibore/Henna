package me.ibore.henna.converter;

import java.io.File;
import java.io.IOException;

import me.ibore.henna.Converter;
import me.ibore.henna.HttpUtils;
import okhttp3.Response;
import okio.Okio;

public class FileConverter implements Converter<File> {

    private File fileDir;

    private FileConverter(File fileDir) {
        if (fileDir.isDirectory()) {
            this.fileDir = fileDir;
        } else {
            throw new NullPointerException("this file not is directory");
        }
    }

    public static FileConverter create(File filePath) {
        return new FileConverter(filePath);
    }

    @Override
    public File convert(Response value) throws IOException {
        File tempFile = new File(fileDir, File.separator + HttpUtils.getNetFileName(value, value.request().url().toString()));
        Okio.buffer(Okio.sink(tempFile)).writeAll(Okio.buffer(Okio.source(value.body().byteStream())));
        return tempFile;
    }
}
