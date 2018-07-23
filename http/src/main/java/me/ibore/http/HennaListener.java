package me.ibore.http;


import me.ibore.http.progress.Progress;
import me.ibore.http.progress.ProgressListener;

public abstract class HennaListener<T> implements ProgressListener {

    public void onStart(Request<T, ? extends Request> request) {

    }

    public abstract void onResponse(Call<T> call, Response<T> response);

    public abstract void onFailure(Call<T> call, Throwable e);

    public void onFinish() {

    }

    @Override
    public void onProgress(Progress progress) {

    }
}
