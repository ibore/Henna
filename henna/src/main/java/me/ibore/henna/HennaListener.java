package me.ibore.henna;


public abstract class HennaListener<T> {

    public void onStart(Request<T, ? extends Request> request) {

    }

    public abstract void onResponse(Call<T> call, Response<T> response);

    public abstract void onFailure(Call<T> call, Throwable e);

    public void onFinish() {

    }
}
