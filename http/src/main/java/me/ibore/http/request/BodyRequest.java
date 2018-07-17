package me.ibore.http.request;

import com.google.gson.Gson;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ibore.http.Henna;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.progress.ProgressRequestBody;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class BodyRequest extends Request<BodyRequest> {

    private RequestBody requestBody;
    private MultipartBody multipartBody;
    private ProgressListener uploadListener;
    private LinkedHashMap<String, List<FileWrapper>> fileParams;

    public BodyRequest(Henna henna) {
        super(henna);
        fileParams = new LinkedHashMap<>();
    }

    public BodyRequest requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public BodyRequest multipartBody(MultipartBody multipartBody) {
        this.multipartBody = multipartBody;
        return this;
    }

    public BodyRequest upFile(File file) {
        this.requestBody = RequestBody.create(MEDIA_TYPE_STREAM, file);
        return this;
    }

    public BodyRequest upString(String content) {
        this.requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, content);
        return this;
    }

    public BodyRequest upString(MediaType mediaType, String content) {
        this.requestBody = RequestBody.create(mediaType, content);
        return this;
    }

    public BodyRequest upJson(Object json) {
        this.requestBody = RequestBody.create(MEDIA_TYPE_JSON, new Gson().toJson(json));
        return this;
    }

    public BodyRequest upBytes(byte[] bytes) {
        this.requestBody = RequestBody.create(MEDIA_TYPE_STREAM, bytes);
        return this;
    }

    public BodyRequest param(String key, File file) {
        return param(key, file, file.getName());
    }

    private BodyRequest param(String key, File file, String fileName) {
        MediaType mediaType;
        String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
        if (contentType == null) {
            mediaType = MEDIA_TYPE_STREAM;
        } else {
            mediaType =  MediaType.parse(contentType);
        }
        return param(key, file, fileName, mediaType);
    }

    private BodyRequest param(String key, File file, String fileName, MediaType mediaType) {
        //解决文件名中含有#号异常的问题
        return param(key, new FileWrapper(file, fileName.replace("#", ""),
                mediaType == null ? MEDIA_TYPE_STREAM : mediaType));
    }

    public BodyRequest param(String key, FileWrapper fileWrapper) {
        if (key != null) {
            List<FileWrapper> fileWrappers = fileParams.get(key);
            if (null == fileWrappers) {
                fileWrappers = new ArrayList<>();
                fileParams.put(key, fileWrappers);
            }
            fileWrappers.add(fileWrapper);
        }
        return this;
    }

    public void removeFileParams(String key) {
        fileParams.remove(key);
    }

    public LinkedHashMap<String, List<FileWrapper>> fileParams() {
        return fileParams;
    }

    public BodyRequest params(String key, List<FileWrapper> values) {
        if (key != null && values != null && !values.isEmpty()) {
            for (FileWrapper value : values) {
                param(key, value.getFile(), value.getFileName(), value.getContentType());
            }
        }
        return this;
    }

    public BodyRequest listener(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
        return this;
    }

    @Override
    protected okhttp3.Request.Builder generateRequest(HennaListener listener) {
        if (null == requestBody) requestBody = generateRequestBody();
        if (null != uploadListener) {
            requestBody = new ProgressRequestBody(henna.getDelivery(), requestBody, uploadListener, refreshTime);
        }
        return new okhttp3.Request.Builder()
                .method(method, requestBody)
                .url(url)
                .tag(tag)
                .cacheControl(null == cacheControl ? new CacheControl.Builder().noCache().build() : cacheControl)
                .headers(generateHeaders());
    }

    private RequestBody generateRequestBody() {
        if (fileParams.isEmpty() && null == multipartBody) {
            //表单提交，没有文件
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : urlParams.keySet()) {
                List<String> urlValues = urlParams.get(key);
                for (String value : urlValues) {
                    bodyBuilder.add(key, value);
                }
            }
            return bodyBuilder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MEDIA_TYPE_FORM);
            for (MultipartBody.Part part : multipartBody.parts()) {
                builder.addPart(part);
            }
            if (!urlParams.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : urlParams.entrySet()) {
                    List<String> urlValues = entry.getValue();
                    for (String value : urlValues) {
                        builder.addFormDataPart(entry.getKey(), value);
                    }
                }
            }
            for (Map.Entry<String, List<FileWrapper>> entry : fileParams.entrySet()) {
                List<FileWrapper> fileValues = entry.getValue();
                for (FileWrapper fileWrapper : fileValues) {
                    RequestBody fileBody = RequestBody.create(fileWrapper.getContentType(), fileWrapper.getFile());
                    builder.addFormDataPart(entry.getKey(), fileWrapper.getFileName(), fileBody);
                }
            }
            return builder.build();
        }
    }

}
