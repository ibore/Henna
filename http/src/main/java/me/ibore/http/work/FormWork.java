package me.ibore.http.work;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class FormWork extends Work {

    @Override
    Object invoke(Method method, Object[] args) {
        //请求地址
        String url = null;
        //请求方式
        String md = null;
        if (method.isAnnotationPresent(GET.class)) {
            url = method.getAnnotation(GET.class).value();
            md = Const.GET;
        }

        if (url == null && method.isAnnotationPresent(POST.class)) {
            url = method.getAnnotation(POST.class).value();
            md = Const.POST;
        }

        if (url == null) {
            throw new RuntimeException("url is null");
        }

        if (!url.startsWith("http")) {
            url = baseUrl + url;
        }



        //解析参数
        Annotation[] annotations = checkoutParameter(method);
        int len = annotations.length;
        //参数map
        Map<String, List<String>> params = new HashMap<>();
        //请求头map
        Headers headers = getHeaders(annotations, args);

        for (int i = 0; i < len; i++) {
            Annotation annotation = annotations[0];
            if (annotation instanceof Field) {
                params.put(((Field) annotation).value(), (String) args[i]);
            } else if (annotation instanceof FieldMap) {
                params.putAll((Map<? extends String, ? extends List<String>>) args[i]);
            }
        }

        //返回值类型
        final Type returnType = getReturnType(method);


        //构建请求执行访问操作
        return new FormRequest()
                .url(url)
                .method(md)
                .headers(headers)
                .client(client)
                .params(params)
                .observerResponseBody()
                .map(new Function<ResponseBody, Object>() {
                    @Override
                    public Object apply(ResponseBody responseBody) throws Exception {
                        if (responseBodyConvert != null) {
                            return responseBodyConvert.convert(responseBody, returnType);
                        } else {
                            return gson.fromJson(responseBody.string(), returnType);
                        }
                    }
                });
    }
}
