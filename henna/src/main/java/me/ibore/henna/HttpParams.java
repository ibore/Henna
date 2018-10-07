package me.ibore.henna;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

public class HttpParams implements Serializable {
    private static final long serialVersionUID = 7369819159227055048L;

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    /** 普通的键值对参数 */
    public LinkedHashMap<String, List<String>> urlParamsMap;

    /** 文件的键值对参数 */
    public LinkedHashMap<String, List<FileItem>> fileParamsMap;

    public HttpParams() {
        urlParamsMap = new LinkedHashMap<>();
        fileParamsMap = new LinkedHashMap<>();
    }

    public void put(HttpParams params) {
        if (params != null) {
            if (params.urlParamsMap != null && !params.urlParamsMap.isEmpty()) urlParamsMap.putAll(params.urlParamsMap);
            if (params.fileParamsMap != null && !params.fileParamsMap.isEmpty()) fileParamsMap.putAll(params.fileParamsMap);
        }
    }

    public void put(Map<String, String> params, boolean... isReplace) {
        if (params == null || params.isEmpty()) return;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            put(entry.getKey(), entry.getValue(), isReplace);
        }
    }

    public void put(String key, String value, boolean... isReplace) {
        if (key != null && value != null) {
            List<String> urlValues = urlParamsMap.get(key);
            if (urlValues == null) {
                urlValues = new ArrayList<>();
                urlParamsMap.put(key, urlValues);
            }
            if (isReplace != null && isReplace.length > 0) {
                if (isReplace[0]) urlValues.clear();
            } else {
                urlValues.clear();
            }
            urlValues.add(value);
        }

    }

    public void put(String key, int value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, long value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, float value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, double value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, char value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, boolean value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, List<String> values, boolean... isReplace) {
        if (key != null && values != null && !values.isEmpty()) {
            for (String value : values) {
                put(key, value, isReplace);
            }
        }
    }

    public void putFile(String key, File file, boolean... isReplace) {
        putFile(key, file, file.getName(), isReplace);
    }

    public void putFile(String key, File file, String fileName, boolean... isReplace) {
        putFile(key, file, fileName, HennaUtils.guessMimeType(fileName), isReplace);
    }

    public void putFile(String key, File file, String fileName, MediaType contentType, boolean... isReplace) {
        if (key != null && file != null && fileName != null && contentType != null) {
            List<FileItem> fileItems = fileParamsMap.get(key);
            if (fileItems == null) {
                fileItems = new ArrayList<>();
                fileParamsMap.put(key, fileItems);
            }
            if (isReplace != null && isReplace.length > 0) {
                if (isReplace[0]) fileItems.clear();
            } else {
                fileItems.clear();
            }
            fileItems.add(new FileItem(file, fileName, contentType));
        }
    }

    public void putFile(String key, FileItem fileItem, boolean... isReplace) {
        if (key != null && fileItem != null) {
            putFile(key, fileItem.file, fileItem.fileName, fileItem.contentType, isReplace);
        }
    }

    public void putFiles(String key, List<File> files, boolean... isReplace) {
        if (key != null && files != null && !files.isEmpty()) {
            for (File file : files) {
                putFile(key, file, isReplace);
            }
        }
    }

    public void putFileItems(String key, List<FileItem> fileItems, boolean... isReplace) {
        if (key != null && fileItems != null && !fileItems.isEmpty()) {
            for (FileItem fileItem : fileItems) {
                putFile(key, fileItem, isReplace);
            }
        }
    }

    public void removeUrl(String key) {
        urlParamsMap.remove(key);
    }

    public void removeFile(String key) {
        fileParamsMap.remove(key);
    }

    public void remove(String key) {
        removeUrl(key);
        removeFile(key);
    }

    public void clear() {
        urlParamsMap.clear();
        fileParamsMap.clear();
    }

    /** 文件类型的包装类 */
    public static class FileItem implements Serializable {

        public File file;
        public String fileName;
        public transient MediaType contentType;
        public long fileSize;

        public FileItem(File file, String fileName, MediaType contentType) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSize = file.length();
        }

        @Override
        public String toString() {
            return "FileItem{" +
                    "file=" + file +
                    ", fileName='" + fileName + '\'' +
                    ", contentType=" + contentType +
                    ", fileSize=" + fileSize +
                    '}';
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, List<String>> entry : urlParamsMap.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        for (ConcurrentHashMap.Entry<String, List<FileItem>> entry : fileParamsMap.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }

}
