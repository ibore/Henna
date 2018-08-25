package me.ibore.henna;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okio.Okio;

public class FileConverter implements Converter<File> {

    private File filePath;

    private FileConverter(String filePath) {
        this.filePath = new File(filePath);
        if (!this.filePath.isDirectory()) {
            throw new NullPointerException("this file not is directory");
        }
    }

    public static FileConverter create() {
        return new FileConverter(HttpUtils.getDefaultFilePath());
    }

    public static FileConverter create(String filePath) {
        return new FileConverter(filePath);
    }

    @Override
    public File convert(Response value) throws IOException {
        File tempFile = new File(filePath, HttpUtils.getNetFileName(value, value.request().url().toString()));
        InputStream is = value.body().byteStream();
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = new FileOutputStream(tempFile);
        while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
        }
        fos.flush();
        return tempFile;
    }
}
