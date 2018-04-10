package me.ibore.http.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.MediaType;

public final class HttpParams implements Serializable {

    /**
     * 普通的键值对参数
     */
    public LinkedHashMap<String, String> urlParamsMap;
    /**
     * 文件的键值对参数
     */
    public LinkedHashMap<String, List<FileWrapper>> fileParamsMap;

    public HttpParams() {
//        init();
    }

    public HttpParams(String key, String value) {
//        init();
//        put(key, value);
    }



    public static class FileWrapper<T> {
        public T file;//可以是
        public String fileName;
        public MediaType contentType;
        public long fileSize;
    }


}
