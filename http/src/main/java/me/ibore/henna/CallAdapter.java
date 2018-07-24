package me.ibore.henna;

import me.ibore.http.R;

public interface CallAdapter<T, E> {

    E adapter(Call<T> call, boolean isAsync);

}
