package me.ibore.http.listener;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

public abstract class JsonListener<T> extends AbsHttpListener<T> {

    @Override
    public T convert(ResponseBody responseBody) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        String content = responseBody.string();
        return new Gson().fromJson(content, type);
    }

}
