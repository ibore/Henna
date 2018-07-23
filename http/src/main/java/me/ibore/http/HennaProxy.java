package me.ibore.http;

import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.ibore.http.adapter.rxjava2.RxJava2CallAdapter;
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
import okhttp3.internal.http.HttpMethod;

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
        } else {
            throw new RuntimeException("you need to add annotation ( @Query || @Form || @Multipart ) to declare the quest Type");
        }
        if (TextUtils.isEmpty(url)) throw new RuntimeException("url is null");
        if (!url.startsWith("http")) url = baseUrl + url;
        if (HttpUtils.hasBody(httpMethod)) {
            return invokeBody(httpMethod, url, method, args);
        } else {
            return invokeNoBody(httpMethod, url, method, args);
        }

    }

    private Object invokeNoBody(String httpMethod, String url, Method method, Object[] args) {
        RequestNoBody request = new RequestNoBody<>(henna)
                .method(httpMethod)
                .url(url);
        Annotation[] annotations = checkoutParameter(method);
        request.headers(getHeaders(annotations, args));
        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (annotation instanceof Header) {

            } else if (annotation instanceof Param) {
                request.params(((Param) annotation).value(), (String) args[i]);
            } else if (annotation instanceof ParamMap) {
                request.params((Map<String, String>) args[i], false);
            } else {
                throw new RuntimeException("");
            }
        }
        return request.adapter(new RxJava2CallAdapter());
    }

    private Object invokeBody(String httpMethod, String url, Method method, Object[] args) {
        RequestHasBody request = new RequestHasBody<>(henna)
                .method(httpMethod)
                .url(url);
        Annotation[] annotations = checkoutParameter(method);
        request.headers(getHeaders(annotations, args));

        for (int i = 0; i < annotations.length; i++) {
            Annotation annotation = annotations[i];
            if (annotation instanceof Header) {
                request.headers(((Header) annotation).value(), (String) args[i]);
            } else if (annotation instanceof HeaderMap) {
                Map<? extends String, ? extends String> map = (Map<? extends String, ? extends String>) args[i];
                for (String key : map.keySet()) {
                    request.headers(key, map.get(key));
                }
            }  else if (annotation instanceof Param) {
                request.params((String) args[i], false);
            } else if (annotation instanceof ParamMap) {
                request.params((Map<String, List<String>>) args[i], false);
            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof ParamMap) {

            }  else if (annotation instanceof Listener) {
                request.uploadListener((ProgressListener) args[i]);
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

    protected HttpHeaders getHeaders(Annotation[] annotations, Object[] args) {
        HttpHeaders headers = new HttpHeaders();
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
