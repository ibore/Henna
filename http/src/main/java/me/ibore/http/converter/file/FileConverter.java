package me.ibore.http.converter.file;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class FileConverter implements Converter<ResponseBody, File> {

    /**
     * 添加请求头的key,后面数字为了防止重复
     */
    public static final String SAVE_PATH = "savePath2016050433191";

    static final FileConverter INSTANCE = new FileConverter();

    @Override
    public File convert(ResponseBody value) throws IOException {
        return null;
    }


}
