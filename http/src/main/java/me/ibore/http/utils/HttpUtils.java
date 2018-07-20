package me.ibore.http.utils;

import android.os.Handler;
import android.os.Looper;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import me.ibore.http.HttpHeaders;
import me.ibore.http.HttpParams;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    /** 将传递进来的参数拼接成 url */
    public static String createUrlFromParams(String url, Map<String, List<String>> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, List<String>> urlParams : params.entrySet()) {
                List<String> urlValues = urlParams.getValue();
                for (String value : urlValues) {
                    //对参数进行 utf-8 编码,防止头信息传中文
                    String urlValue = URLEncoder.encode(value, "UTF-8");
                    sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /** 通用的拼接请求头 */
    public static Headers appendHeaders(HttpHeaders headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (null != headers) {
            try {
                for (Map.Entry<String, String> entry : headers.headersMap.entrySet()) {
                    //对头信息进行 utf-8 编码,防止头信息传中文,这里暂时不编码,可能出现未知问题,如有需要自行编码
//                String headerValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                    headerBuilder.add(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return headerBuilder.build();
    }

    /** 生成类似表单的请求体 */
    public static RequestBody generateMultipartRequestBody(HttpParams params, boolean isMultipart) {
        if (params.fileParamsMap.isEmpty() && !isMultipart) {
            //表单提交，没有文件
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : params.urlParamsMap.keySet()) {
                List<String> urlValues = params.urlParamsMap.get(key);
                for (String value : urlValues) {
                    bodyBuilder.addEncoded(key, value);
                }
            }
            return bodyBuilder.build();
        } else {
            //表单提交，有文件
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //拼接键值对
            if (!params.urlParamsMap.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : params.urlParamsMap.entrySet()) {
                    List<String> urlValues = entry.getValue();
                    for (String value : urlValues) {
                        multipartBodybuilder.addFormDataPart(entry.getKey(), value);
                    }
                }
            }
            //拼接文件
            for (Map.Entry<String, List<HttpParams.FileWrapper>> entry : params.fileParamsMap.entrySet()) {
                List<HttpParams.FileWrapper> fileValues = entry.getValue();
                for (HttpParams.FileWrapper fileWrapper : fileValues) {
                    RequestBody fileBody = RequestBody.create(fileWrapper.contentType, fileWrapper.file);
                    multipartBodybuilder.addFormDataPart(entry.getKey(), fileWrapper.fileName, fileBody);
                }
            }
            return multipartBodybuilder.build();
        }
    }

    private static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header(HttpHeaders.HEAD_KEY_CONTENT_DISPOSITION);
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replaceAll("\"", "");
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
            }
            split = "filename*=";
            indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                String fileName = dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
                String encode = "UTF-8''";
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length(), fileName.length());
                }
                return fileName;
            }
        }
        return null;
    }

    /** 根据文件名获取MIME类型 */
    public static MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        fileName = fileName.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return HttpParams.MEDIA_TYPE_STREAM;
        }
        return MediaType.parse(contentType);
    }

    public static final Handler mDelivery = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        mDelivery.post(runnable);
    }
}
