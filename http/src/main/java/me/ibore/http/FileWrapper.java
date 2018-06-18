package me.ibore.http;

import java.io.File;

import okhttp3.MediaType;

public class FileWrapper {

    private File file;
    private String fileName;
    private MediaType contentType;

    public FileWrapper(File file, String fileName, MediaType contentType) {
        this.file = file;
        this.fileName = fileName;
        this.contentType = contentType;
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

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

}
