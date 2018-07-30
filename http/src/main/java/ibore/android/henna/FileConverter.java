package ibore.android.henna;

import java.io.File;
import java.io.IOException;

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
        File tempFile = new File(filePath, HttpUtils.getNetFileName(value, value.request().url().toString()));
        Okio.buffer(Okio.sink(tempFile)).writeAll(Okio.buffer(Okio.source(value.body().byteStream())));
        return tempFile;
    }
}
