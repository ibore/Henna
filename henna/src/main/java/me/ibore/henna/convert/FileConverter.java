package me.ibore.henna.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.ibore.henna.Converter;
import me.ibore.henna.HennaUtils;
import me.ibore.henna.exception.ConvertException;
import okhttp3.Response;

public class FileConverter implements Converter<File> {

    private File fileDir;

    private FileConverter(String fileDir) {
        this.fileDir = new File(fileDir);
        if (!this.fileDir.isDirectory()) {
            throw new NullPointerException("this file not is directory");
        }
    }

    public static FileConverter create() {
        return new FileConverter(HennaUtils.getDefaultFileDir());
    }

    public static FileConverter create(String fileDir) {
        return new FileConverter(fileDir);
    }

    @Override
    public File convert(Response value) throws IOException, ConvertException {
        try {
            File tempFile = new File(fileDir, HennaUtils.getUrlFileName(value.request().url().toString()));
            InputStream is = value.body().byteStream();
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = new FileOutputStream(tempFile);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return tempFile;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ConvertException("Convert Error", e);
        }
    }
}
