package me.ibore.henna;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    /** 根据响应头或者url获取文件名 */
    public static String getNetFileName(Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (TextUtils.isEmpty(fileName)) fileName = getUrlFileName(url);
        if (TextUtils.isEmpty(fileName)) fileName = "temp_" + System.currentTimeMillis();
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
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
            String splits[] = dispositionHeader.split(";");
            String split = null;
            for (String s: splits) {
                if (null == split && (s.contains("filename=") || s.contains("filename*="))) {
                    split = s;
                }
            }
            if (null == split) {
                return null;
            } else if (split.contains("filename=")){
                return split.replace("filename=", "");
            } else if (split.contains("filename*=")){
                split = split.replace("filename*=", "");
                String encode = "UTF-8''";
                if (split.startsWith(encode)) {
                    return split.substring(encode.length(), split.length());
                }
            }
        }
        return null;
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
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

    /** 根据路径删除文件 */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) return true;
        File file = new File(path);
        if (!file.exists()) return true;
        if (file.isFile()) {
            return file.delete();
        }
        return false;
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

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static final Handler mDelivery = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        mDelivery.post(runnable);
    }

    public static boolean hasBody(String method) {
        switch (method) {
            case "POST":
            case "PUT":
            case "PATCH":
            case "DELETE":
            case "OPTIONS":
                return true;
            default:
                return false;
        }
    }

    /**
     * 从url获取 如果url为空，则文件名为当前时间毫秒值
     *
     * @param url download file url
     * @return file name
     */
    public static String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return System.currentTimeMillis() + "";
    }

    public static String getDefaultFilePath() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
        File file = new File(filePath);
        if (!file.exists()) {
            boolean createDir = file.mkdirs();
            if (createDir) {
                if (BuildConfig.DEBUG) Log.d("Henna", "create file dir success");
            }
        }
        return filePath;
    }

    public static void close(Closeable... closeables) {
        for (Closeable io : closeables) {
            if (io != null) {
                try {
                    io.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
