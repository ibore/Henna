package me.ibore.http.request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import me.ibore.http.HttpParams;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.progress.ProgressRequestBody;
import me.ibore.http.utils.HttpUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static me.ibore.http.HttpParams.MEDIA_TYPE_JSON;
import static me.ibore.http.HttpParams.MEDIA_TYPE_PLAIN;
import static me.ibore.http.HttpParams.MEDIA_TYPE_STREAM;

public class BodyRequest<T> extends Request<T, BodyRequest<T>> {

    private RequestBody requestBody;
    protected ProgressListener uploadListener;
    private boolean isMultipart;

    @SuppressWarnings("unchecked")
    public BodyRequest upRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upString(String string) {
        requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, string);
        return this;
    }
    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     * 该方法用于定制请求content-type
     */
    @SuppressWarnings("unchecked")
    public BodyRequest upString(String string, MediaType mediaType) {
        requestBody = RequestBody.create(mediaType, string);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upJson(String json) {
        requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upJson(JSONObject jsonObject) {
        requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonObject.toString());
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upJson(JSONArray jsonArray) {
        requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonArray.toString());
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upBytes(byte[] bs) {
        requestBody = RequestBody.create(MEDIA_TYPE_STREAM, bs);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upBytes(byte[] bs, MediaType mediaType) {
        requestBody = RequestBody.create(mediaType, bs);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upFile(File file) {
        requestBody = RequestBody.create(HttpUtils.guessMimeType(file.getName()), file);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public BodyRequest upFile(File file, MediaType mediaType) {
        requestBody = RequestBody.create(mediaType, file);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest isMultipart(boolean isMultipart) {
        this.isMultipart = isMultipart;
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest params(String key, File file) {
        params.put(key, file);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest addFileParams(String key, List<File> files) {
        params.putFileParams(key, files);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers) {
        params.putFileWrapperParams(key, fileWrappers);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest params(String key, File file, String fileName) {
        params.put(key, file, fileName);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest params(String key, File file, String fileName, MediaType contentType) {
        params.put(key, file, fileName, contentType);
        return this;
    }

    @SuppressWarnings("unchecked")
    public BodyRequest upload(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
        return this;
    }

    @Override
    protected okhttp3.Request generateRequest() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.method(method, generateRequestBody())
                .url(HttpUtils.createUrlFromParams(url, params.urlParamsMap))
                .tag(tag)
                .headers(HttpUtils.appendHeaders(headers));
        return builder.build();
    }

    private RequestBody generateRequestBody() {
        if (null == requestBody) {
            requestBody = HttpUtils.generateMultipartRequestBody(params, isMultipart);
        }
        if (null != uploadListener) {
            requestBody = ProgressRequestBody.create(isUIThread ? HttpUtils.mDelivery : null, requestBody, uploadListener, refreshTime);
        }
        return requestBody;
    }

}
