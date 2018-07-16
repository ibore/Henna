package me.ibore.http.request;

import com.google.gson.Gson;

import org.json.JSONTokener;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.ibore.http.XHttp;
import me.ibore.http.listener.AbsHttpListener;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.progress.ProgressRequestBody;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class PostRequest extends Request<PostRequest> {

    private RequestBody requestBody;
    private boolean isMultipart;
    private ProgressListener uploadListener;

    public PostRequest(XHttp http) {
        super(http);
    }

    public PostRequest isMultipart(boolean multipart) {
        isMultipart = multipart;
        return this;
    }

    public PostRequest requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public PostRequest upFile(File file) {
        this.requestBody = RequestBody.create(MEDIA_TYPE_STREAM, file);
        return this;
    }

    public PostRequest upString(String content) {
        return upString(MEDIA_TYPE_PLAIN, content);
    }

    public PostRequest upString(MediaType mediaType, String content) {
        this.requestBody = RequestBody.create(mediaType, content);
        return this;
    }

    public PostRequest upJson(Object json) {
        return upString(MEDIA_TYPE_JSON, XHttp.jsonParser().toJson(json));
    }

    public PostRequest upBytes(byte[] bytes) {
        this.requestBody = RequestBody.create(MEDIA_TYPE_STREAM, bytes);
        return this;
    }

    public PostRequest param(String key, List<File> values) {
        if (key != null && values != null && !values.isEmpty()) {
            for (File value : values) {
                param(key, value);
            }
        }
        return this;
    }

    public PostRequest params(String key, List<FileWrapper> values) {
        if (key != null && values != null && !values.isEmpty()) {
            for (FileWrapper value : values) {
                param(key, value.getFile(), value.getFileName(), value.getContentType());
            }
        }
        return this;
    }

    public PostRequest param(String key, File file) {
        return param(key, file, file.getName());
    }

    public PostRequest param(String key, FileWrapper fileWrapper) {
        return param(key, fileWrapper.getFile(), fileWrapper.getFileName(), fileWrapper.getContentType());
    }

    private PostRequest param(String key, File file, String fileName) {
        MediaType mediaType;
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        fileName = fileName.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            mediaType = MEDIA_TYPE_STREAM;
        }
        mediaType =  MediaType.parse(contentType);
        return param(key, file, fileName, mediaType);
    }

    private PostRequest param(String key, File file, String fileName, MediaType contentType) {
        if (key != null) {
            List<FileWrapper> fileWrappers = fileParams.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParams.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper(file, fileName, contentType));
        }
        return this;
    }

    public PostRequest upload(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
        return this;
    }

    @Override
    protected okhttp3.Request.Builder generateRequest(AbsHttpListener listener) {
        if (null == requestBody) requestBody = generateRequestBody();
        if (null != uploadListener) {
            requestBody = new ProgressRequestBody(http.getDelivery(), requestBody, uploadListener, refreshTime);
        }
        return new okhttp3.Request.Builder()
                .method(method, requestBody)
                .url(url)
                .tag(tag)
                .cacheControl(null == cacheControl ? new CacheControl.Builder().noCache().build() : cacheControl)
                .headers(generateHeaders());
    }

    private RequestBody generateRequestBody() {
        if (fileParams.isEmpty() && !isMultipart) {
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
            //表单提交，有文件
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //拼接键值对
            if (!urlParams.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : urlParams.entrySet()) {
                    List<String> urlValues = entry.getValue();
                    for (String value : urlValues) {
                        multipartBodybuilder.addFormDataPart(entry.getKey(), value);
                    }
                }
            }
            //拼接文件
            for (Map.Entry<String, List<FileWrapper>> entry : fileParams.entrySet()) {
                List<FileWrapper> fileValues = entry.getValue();
                for (FileWrapper fileWrapper : fileValues) {
                    RequestBody fileBody = RequestBody.create(fileWrapper.getContentType(), fileWrapper.getFile());
                    multipartBodybuilder.addFormDataPart(entry.getKey(), fileWrapper.getFileName(), fileBody);
                }
            }
            return multipartBodybuilder.build();
        }
    }

}
