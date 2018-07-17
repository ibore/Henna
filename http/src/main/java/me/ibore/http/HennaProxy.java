package me.ibore.http;

import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ibore.http.annotation.DELETE;
import me.ibore.http.annotation.GET;
import me.ibore.http.annotation.HEAD;
import me.ibore.http.annotation.Header;
import me.ibore.http.annotation.HeaderMap;
import me.ibore.http.annotation.OPTIONS;
import me.ibore.http.annotation.PATCH;
import me.ibore.http.annotation.POST;
import me.ibore.http.annotation.PUT;
import me.ibore.http.annotation.Param;
import me.ibore.http.annotation.ParamMap;
import me.ibore.http.annotation.TRACE;
import me.ibore.http.annotation.Listener;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.request.BodyRequest;
import me.ibore.http.request.NoBodyRequest;

public class HennaProxy {

    private String baseUrl;
    private Henna henna;

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
        if (method.isAnnotationPresent(GET.class)) {
            httpMethod = HttpMethod.GET;
            url = method.getAnnotation(GET.class).value();
        } else if (method.isAnnotationPresent(HEAD.class)) {
            httpMethod = HttpMethod.HEAD;
            url = method.getAnnotation(HEAD.class).value();
        } else if (method.isAnnotationPresent(TRACE.class)) {
            httpMethod = HttpMethod.TRACE;
            url = method.getAnnotation(TRACE.class).value();
        } else if (method.isAnnotationPresent(POST.class)) {
            httpMethod = HttpMethod.POST;
            url = method.getAnnotation(POST.class).value();
        } else if (method.isAnnotationPresent(PUT.class)) {
            httpMethod = HttpMethod.PUT;
            url = method.getAnnotation(PUT.class).value();
        } else if (method.isAnnotationPresent(DELETE.class)) {
            httpMethod = HttpMethod.DELETE;
            url = method.getAnnotation(DELETE.class).value();
        } else if (method.isAnnotationPresent(OPTIONS.class)) {
            httpMethod = HttpMethod.OPTIONS;
            url = method.getAnnotation(OPTIONS.class).value();
        } else if (method.isAnnotationPresent(PATCH.class)) {
            httpMethod = HttpMethod.PATCH;
            url = method.getAnnotation(PATCH.class).value();
        } else {
            throw new RuntimeException("you need to add annotation ( @Query || @Form || @Multipart ) to declare the quest Type");
        }
        if (TextUtils.isEmpty(url)) throw new RuntimeException("url is null");
        if (!url.startsWith("http")) url = baseUrl + url;
        if (HttpMethod.hasRequestBody(httpMethod)) {
            return invokeBody(httpMethod, url, method, args);
        } else {
            return invokeNoBody(httpMethod, url, method, args);
        }

    }

    private Object invokeNoBody(String httpMethod, String url, Method method, Object[] args) {
        NoBodyRequest request = new NoBodyRequest(henna).method(httpMethod).url(url);
        Annotation[] annotations = checkoutParameter(method);
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (annotation instanceof Param) {
                request.param(((Param) annotation).value(), (String) args[i]);
            } else if (annotation instanceof ParamMap) {
                request.params((Map<String, List<String>>) args[i], false);
            } else {
                throw new RuntimeException("@MultiPart just can be annotation at MultipartBody parameter");
            }
        }
        return null;
    }

    private Object invokeBody(String httpMethod, String url, Method method, Object[] args) {
        BodyRequest request = new BodyRequest(henna).method(httpMethod).url(url);
        Annotation[] annotations = checkoutParameter(method);
        request.header(getHeaders(annotations, args));
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (annotation instanceof Param) {
                if (args[i] instanceof String) {
                    request.param(((Header) annotation).value(), (String) args[i]);
                } else if (args[i] instanceof Double){
                    request.param(((Header) annotation).value(), Double.toString((Double) args[i]));
                } else if (args[i] instanceof Integer){
                    request.param(((Header) annotation).value(), Integer.toString((Integer) args[i]));
                } else if (args[i] instanceof Float){
                    request.param(((Header) annotation).value(), Float.toString((Float) args[i]));
                } else if (args[i] instanceof Long){
                    request.param(((Header) annotation).value(), Long.toString((Long) args[i]));
                }
            } else if (annotation instanceof ParamMap) {
                request.params((Map<String, List<String>>) args[i], false);
            } else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof Listener) {
                request.listener((ProgressListener) args[i]);
            } else {
                throw new RuntimeException("@MultiPart just can be annotation at MultipartBody parameter");
            }
        }

        return null;
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

    protected Map<String, String> getHeaders(Annotation[] annotations, Object[] args) {
        Map<String, String> headers = new LinkedHashMap<>();
        int len = annotations.length;
        for (int i = 0; i < len; i++) {
            Annotation annotation = annotations[0];
            if (annotation instanceof Header) {
                headers.put(((Header) annotation).value(), (String) args[i]);
            } else if (annotation instanceof HeaderMap) {
                Map<? extends String, ? extends String> map = (Map<? extends String, ? extends String>) args[i];
                for (String key : map.keySet()) {
                    headers.put(key, map.get(key));
                }
            }
        }
        return headers;
    }
}
