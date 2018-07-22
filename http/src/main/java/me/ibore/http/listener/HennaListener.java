package me.ibore.http.listener;


import me.ibore.http.Response;
import me.ibore.http.call.Call;
import me.ibore.http.progress.ProgressListener;
import me.ibore.http.request.Request;

public interface HennaListener<T> extends ProgressListener {

    void onStart(Request<T, ? extends Request> request);

    void onResponse(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable e);

    void onFinish();

}
