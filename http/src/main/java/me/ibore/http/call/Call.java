package me.ibore.http.call;

import java.io.IOException;

import me.ibore.http.listener.HennaListener;
import me.ibore.http.request.Request;
import okhttp3.Response;

public interface Call<T> {

    Request<T, ? extends Request> request();

    Response execute() throws IOException;

    void enqueue(HennaListener<T> listener);

    void cancel();

    boolean isExecuted();

    boolean isCanceled();

    okhttp3.Call clone();

    interface Factory<T> {

        okhttp3.Call newCall(Request<T, ? extends Request> request);

    }
}
