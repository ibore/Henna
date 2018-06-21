package me.ibore.http.listener;

import java.io.File;

import okhttp3.ResponseBody;

public abstract class FileListener extends AbsHttpListener<File> {

    @Override
    public File convert(ResponseBody responseBody) throws Exception {
        return null;
    }

}
