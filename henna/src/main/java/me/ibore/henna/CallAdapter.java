package me.ibore.henna;

public interface CallAdapter<T, E> {

    E adapter(Call<T> call, boolean isAsync);

}
