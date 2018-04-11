package me.ibore.http;

import java.io.File;

/**
 * Created by ibore on 18-2-6.
 */

public class DownloadInfo {

    private String url;
    private File file;
    private String fileName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "url='" + url + '\'' +
                ", file=" + file +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
