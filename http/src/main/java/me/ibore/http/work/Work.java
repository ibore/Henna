package me.ibore.http.work;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;
import me.ibore.http.annotation.Header;
import me.ibore.http.annotation.HeaderMap;
import okhttp3.Headers;

public abstract class Work {

    protected String baseUrl;

    /**
     * 校验参数是否合法
     * @param method
     * @return
     */
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

    protected Headers getHeaders(Annotation[] annotations, Object[] args) {
        Headers.Builder headers = new Headers.Builder();
        int len = annotations.length;
        for (int i = 0; i < len; i++) {
            Annotation annotation = annotations[0];
            if (annotation instanceof Header) {
                headers.add(((Header) annotation).value(), (String) args[i]);
            } else if (annotation instanceof HeaderMap) {
                Map<? extends String, ? extends String> map = (Map<? extends String, ? extends String>) args[i];
                for (String key : map.keySet())
                headers.add(key, map.get(key));
            }
        }
        return headers.build();
    }

    /**
     * 解析返回值类型
     *
     * @param method
     * @return
     */
    public Type getReturnType(Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType().equals(Observable.class)) {
                type = pType.getActualTypeArguments()[0];
            }
        }
        return type;
    }

    abstract Object invoke(final Method method, final Object[] args);
}
