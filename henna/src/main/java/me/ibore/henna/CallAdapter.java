package me.ibore.henna;

/**
 * Call的转换器
 * @param <T>
 * @param <E>
 */
public interface CallAdapter<T, E> {

    E adapter(Call<T> call, boolean isAsync);

}
