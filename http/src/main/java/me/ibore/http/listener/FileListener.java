package me.ibore.http.listener;

import android.os.Environment;

import java.io.File;

import okhttp3.ResponseBody;

public abstract class FileListener extends AbsHttpListener<File> {

    private String folder;                  //目标文件存储的文件夹路径
    private String fileName;                //目标文件存储的文件名

    public FileListener(String fileName) {
        this(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
    }

    public FileListener(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }

    @Override
    public File convert(ResponseBody responseBody) throws Exception {
        return null;
    }

}
