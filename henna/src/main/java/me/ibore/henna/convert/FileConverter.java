package me.ibore.henna.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.ibore.henna.Converter;
import me.ibore.henna.HennaUtils;
import me.ibore.henna.exception.ConvertException;
import okhttp3.Response;
import okhttp3.internal.cache.DiskLruCache;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileConverter implements Converter<File> {

    private String fileDir;
    private File tempFile;
    private boolean isBeginning = false;

    private FileConverter(String fileDir) {
        this.fileDir = fileDir;
    }

    private FileConverter(File tempFile, boolean isBeginning) {
        this.tempFile = tempFile;
        this.isBeginning = isBeginning;
    }

    public static FileConverter create() {
        return new FileConverter(HennaUtils.getDefaultFileDir());
    }

    public static FileConverter create(String fileDir) {
        DiskLruCache
        return new FileConverter(fileDir);
    }

    public static FileConverter create(File tempFile, boolean isBeginning) {
        return new FileConverter(tempFile, isBeginning);
    }

    @Override
    public File convert(Response value) throws IOException, ConvertException {
        try {
            if (null == tempFile) {
                tempFile = new File(fileDir, HennaUtils.getFileNameForResponse(value));
            }
            if (isBeginning) {
                if (tempFile.exists()) tempFile.createNewFile();
            }
            BufferedSink sink = Okio.buffer(Okio.sink(tempFile));
            Buffer buffer = sink.buffer();
            BufferedSource source = value.body().source();
            while (source.read(buffer, 200 * 1024) != -1) {
                sink.emit();
            }
            source.close();
            sink.close();
            return tempFile;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ConvertException("Convert Error", e);
        }
    }
}
