package me.ibore.http.call;

import me.ibore.http.Response;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.request.Request;

public interface Call<T> extends Cloneable {

    Response<T> execute() throws Exception;

    void enqueue(HennaListener<T> listener);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request<T> request();

}
