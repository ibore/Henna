package me.ibore.henna.proxy;

import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import ibore.android.henna.Henna;
import ibore.android.henna.HttpParams;
import ibore.android.henna.HennaUtils;
import ibore.android.henna.ProgressListener;
import ibore.android.henna.RequestHasBody;
import ibore.android.henna.RequestNoBody;
import me.ibore.henna.proxy.http.Body;
import me.ibore.henna.proxy.http.BytesBody;
import me.ibore.henna.proxy.http.DELETE;
import me.ibore.henna.proxy.http.DownloadListener;
import me.ibore.henna.proxy.http.File;
import me.ibore.henna.proxy.http.FileMap;
import me.ibore.henna.proxy.http.FileWrapper;
import me.ibore.henna.proxy.http.FileWrapperList;
import me.ibore.henna.proxy.http.GET;
import me.ibore.henna.proxy.http.HEAD;
import me.ibore.henna.proxy.http.Header;
import me.ibore.henna.proxy.http.HeaderMap;
import me.ibore.henna.proxy.http.JsonBody;
import me.ibore.henna.proxy.http.MultiPart;
import me.ibore.henna.proxy.http.OPTIONS;
import me.ibore.henna.proxy.http.PATCH;
import me.ibore.henna.proxy.http.POST;
import me.ibore.henna.proxy.http.PUT;
import me.ibore.henna.proxy.http.Param;
import me.ibore.henna.proxy.http.ParamMap;
import me.ibore.henna.proxy.http.Path;
import me.ibore.henna.proxy.http.StringBody;
import me.ibore.henna.proxy.http.TRACE;
import me.ibore.henna.proxy.http.UploadListener;
import okhttp3.RequestBody;

public class HennaProxy {

    private String baseUrl;
    private Henna henna;

    public HennaProxy(Henna henna, String baseUrl) {
        this.henna = henna;
        this.baseUrl = baseUrl;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader()
                , new Class[]{service}
                , new InvocationHandler() {
                    public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        return dispatcher(method, args);
                    }
                });
    }

    private Object dispatcher(final Method method, Object[] args) {
        String httpMethod = "";
        String url = "";
        boolean isMultiPart = false;
        if (method.isAnnotationPresent(GET.class)) {
            httpMethod = "GET";
            url = method.getAnnotation(GET.class).value();
        } else if (method.isAnnotationPresent(HEAD.class)) {
            httpMethod = "HEAD";
            url = method.getAnnotation(HEAD.class).value();
        } else if (method.isAnnotationPresent(TRACE.class)) {
            httpMethod = "TRACE";
            url = method.getAnnotation(TRACE.class).value();
        } else if (method.isAnnotationPresent(POST.class)) {
            httpMethod = "POST";
            url = method.getAnnotation(POST.class).value();
        } else if (method.isAnnotationPresent(PUT.class)) {
            httpMethod = "PUT";
            url = method.getAnnotation(PUT.class).value();
        } else if (method.isAnnotationPresent(DELETE.class)) {
            httpMethod = "DELETE";
            url = method.getAnnotation(DELETE.class).value();
        } else if (method.isAnnotationPresent(OPTIONS.class)) {
            httpMethod = "OPTIONS";
            url = method.getAnnotation(OPTIONS.class).value();
        } else if (method.isAnnotationPresent(PATCH.class)) {
            httpMethod = "PATCH";
            url = method.getAnnotation(PATCH.class).value();
        } else if (method.isAnnotationPresent(MultiPart.class)){
            if (HennaUtils.hasBody(httpMethod)) {
                isMultiPart = true;
            } else {
                throw new RuntimeException("this method can not be MultiPart");
            }
        } else {
            throw new RuntimeException("you need to add annotation HTTP method");
        }
        if (TextUtils.isEmpty(url)) throw new RuntimeException("url is null");
        if (!url.startsWith("http")) url = baseUrl + url;
        Annotation[] annotations = checkoutParameter(method);
        if (HennaUtils.hasBody(httpMethod)) {
            RequestHasBody request = new RequestHasBody<>(henna)
                    .method(httpMethod)
                    .isMultipart(isMultiPart)
                    .url(url);
            return invokeHasBody(request, annotations, args);

        } else {
            RequestNoBody request = new RequestNoBody<>(henna)
                    .url(url)
                    .method(httpMethod);
            return invokeNoBody(request, annotations, args);
        }
    }

    @SuppressWarnings("unchecked")
    private Object invokeHasBody(RequestHasBody request, Annotation[] annotations, Object[] args) {
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            Object arg = args[i];
            if (annotation instanceof Header) {
                request.headers(((Header) annotation).value(), (String) arg);
            } else if (annotation instanceof HeaderMap) {
                Map<? extends String, ? extends String> map = (Map<? extends String, ? extends String>) arg;
                for (String key : map.keySet()) {
                    request.headers(key, map.get(key));
                }
            } else if (annotation instanceof Path) {
                String url = request.getUrl();
                request.url(url.replaceAll("\\{" + ((Path) annotation).value() + "}", (String) arg));
            }  else if (annotation instanceof Param) {
                request.params(((Param) annotation).value(), (String) arg);
            } else if (annotation instanceof ParamMap) {
                request.params((Map<String, String>) arg, false);
            } else if (annotation instanceof Body) {
                request.upRequestBody((RequestBody) arg);
            } else if (annotation instanceof BytesBody) {
                request.upBytes((byte[]) arg);
            } else if (annotation instanceof StringBody) {
                request.upString((String) arg);
            } else if (annotation instanceof JsonBody) {
                request.upJson((String) arg);
            } else if (annotation instanceof File) {
                request.params(((File) annotation).value(), (java.io.File) arg);
            } else if (annotation instanceof FileMap) {
                request.addFileWrapperParams(((FileMap) annotation).value(), (List<java.io.File>) arg);
            } else if (annotation instanceof FileWrapper) {
                request.params(((FileWrapper) annotation).value(), (HttpParams.FileWrapper) arg);
            } else if (annotation instanceof FileWrapperList) {
                request.addFileWrapperParams(((FileWrapperList) annotation).value(), (List<HttpParams.FileWrapper>) arg);
            } else if (annotation instanceof UploadListener) {
                request.upload((ProgressListener) arg);
            } else if (annotation instanceof DownloadListener) {
                request.download((ProgressListener) arg);
            } else {
                throw new RuntimeException("you need to add annotation ( @Query || @Form || @Multipart ) to declare the quest Type");
            }
        }
        return request.adapter();
    }

    @SuppressWarnings("unchecked")
    private Object invokeNoBody(RequestNoBody request, Annotation[] annotations, Object[] args) {
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            Object arg = args[i];
            if (annotation instanceof Header) {
                request.headers(((Header) annotation).value(), (String) arg);
            } else if (annotation instanceof HeaderMap) {
                Map<? extends String, ? extends String> map = (Map<? extends String, ? extends String>) arg;
                for (String key : map.keySet()) {
                    request.headers(key, map.get(key));
                }
            } else if (annotation instanceof Path) {
                String url = request.getUrl();
                String replace = "{" + ((Path) annotation).value() + "}";
                String argss = (String) arg;
                request.url(url.replace(replace, argss));
            } else if (annotation instanceof Param) {
                request.params(((Param) annotation).value(), (String) arg);
            } else if (annotation instanceof ParamMap) {
                request.params((Map<String, String>) arg, false);
            } else if (annotation instanceof DownloadListener) {
                request.download((ProgressListener) arg);
            } else {
                throw new RuntimeException("you need to add annotation");
            }
        }
        return request.adapter();
    }

    protected Annotation[] checkoutParameter(Method method) {
        Annotation[][] annSS = method.getParameterAnnotations();
        int len = annSS.length;
        Annotation[] annotations = new Annotation[len];
        for (int i = 0; i < len; i++) {
            if (annSS[i].length < 1) {
                throw new RuntimeException("every parameter need one annotation");
            }
            //每个参数只有第一个注解生效，为了保证参数类型的正确性
            annotations[i] = annSS[i][0];
        }
        return annotations;
    }

}
