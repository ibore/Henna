package ibore.android.henna;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestHasBody<T> extends Request<T, RequestHasBody<T>> {

    private RequestBody requestBody;
    private ProgressListener uploadListener;
    private boolean isMultipart;

    public RequestHasBody() {
        super();
    }

    public RequestHasBody(Henna henna) {
        super(henna);
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upString(String string) {
        requestBody = RequestBody.create(HttpParams.MEDIA_TYPE_PLAIN, string);
        return this;
    }
    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     * 该方法用于定制请求content-type
     */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upString(String string, MediaType mediaType) {
        requestBody = RequestBody.create(mediaType, string);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upJson(String json) {
        requestBody = RequestBody.create(HttpParams.MEDIA_TYPE_JSON, json);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upJson(JSONObject jsonObject) {
        requestBody = RequestBody.create(HttpParams.MEDIA_TYPE_JSON, jsonObject.toString());
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upJson(JSONArray jsonArray) {
        requestBody = RequestBody.create(HttpParams.MEDIA_TYPE_JSON, jsonArray.toString());
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upBytes(byte[] bs) {
        requestBody = RequestBody.create(HttpParams.MEDIA_TYPE_STREAM, bs);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upBytes(byte[] bs, MediaType mediaType) {
        requestBody = RequestBody.create(mediaType, bs);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upFile(File file) {
        requestBody = RequestBody.create(HennaUtils.guessMimeType(file.getName()), file);
        return this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upFile(File file, MediaType mediaType) {
        requestBody = RequestBody.create(mediaType, file);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> isMultipart(boolean isMultipart) {
        this.isMultipart = isMultipart;
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> params(String key, File file) {
        getParams().put(key, file);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> addFileParams(String key, List<File> files) {
        getParams().putFileParams(key, files);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers) {
        getParams().putFileWrapperParams(key, fileWrappers);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> params(String key, File file, String fileName) {
        getParams().put(key, file, fileName);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> params(String key, File file, String fileName, MediaType contentType) {
        getParams().put(key, file, fileName, contentType);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> params(String key, HttpParams.FileWrapper fileWrapper) {
        getParams().put(key, fileWrapper);
        return this;
    }

    @SuppressWarnings("unchecked")
    public RequestHasBody<T> upload(ProgressListener uploadListener) {
        this.uploadListener = uploadListener;
        return this;
    }

    @Override
    protected okhttp3.Request generateRequest() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.method(getMethod(), generateRequestBody())
                .url(getUrl())
                .tag(getTag())
                .headers(HennaUtils.appendHeaders(getHeaders()));
        return builder.build();
    }

    private RequestBody generateRequestBody() {
        if (null == requestBody) {
            requestBody = HennaUtils.generateMultipartRequestBody(getParams(), isMultipart);
        }
        if (null != uploadListener) {
            requestBody = ProgressRequestBody.create(requestBody, uploadListener, isUIThread(), getRefreshTime());
        }
        return requestBody;
    }

}
