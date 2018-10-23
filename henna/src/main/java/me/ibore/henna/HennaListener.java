package me.ibore.henna;


public interface HennaListener<T> {

    void onResponse(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable e);

}
