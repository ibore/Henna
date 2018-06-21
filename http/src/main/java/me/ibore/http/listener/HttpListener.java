package me.ibore.http.listener;

import me.ibore.http.exception.HttpException;

/**
 * Created by Administrator on 2018/2/6.
 */

public interface HttpListener<T> {

    void onSuccess(T t);

    void onError(HttpException e);

}
