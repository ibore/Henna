package me.ibore.http;

public interface CallAdapter<T, R> {

    R adapter(Call<T> call, boolean isAsync);

}
