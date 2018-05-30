package me.ibore.http.converter.file;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import me.ibore.http.progress.ProgressRequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class FileResponseBodyConverter implements Converter<ResponseBody, File> {

    public static final String HEADER_FILE_PATH = "header_file_path";
    public static final String _TMP = ".tmp";
    private static final FileResponseBodyConverter INSTANCE = new FileResponseBodyConverter();

    public static FileResponseBodyConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public File convert(ResponseBody value) throws IOException {
        return writeToDisk(value, getFilePath(value));
    }

    private String getFilePath(ResponseBody responseBody) {
        String filePath = null;
        String fileName = null;
        try {
            Class clazz = responseBody.getClass();
            Field field = clazz.getDeclaredField("delegate");
            field.setAccessible(true);
            ResponseBody body = (ResponseBody) field.get(responseBody);
            if (body instanceof ProgressRequestBody) {
                ProgressRequestBody fileResponseBody = ((ProgressRequestBody) body);
                filePath = fileResponseBody.getFilePath();
                fileName = fileResponseBody.getFileName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 判断文件名是否为空，为空则根据当前时间生成一个临时文件名
        if (TextUtils.isEmpty(fileName)) {
            fileName = System.currentTimeMillis() + _TMP;
        }
        // 判断保存路径是否为文件夹，如果为文件夹则在后面加上请求文件名
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.isDirectory()) {
                filePath = filePath + File.separator + fileName;
            }
        } else {
            // 文件保存路径为空则默认保存到sdcard根目录
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        }
        return filePath;
    }

    private File writeToDisk(ResponseBody body, String filePath) {
        File file = new File(filePath);
        OutputStream outputStream = null;
        byte[] fileReader = new byte[4096];
        InputStream inputStream = body.byteStream();
        try {
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
            }
            outputStream.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}

