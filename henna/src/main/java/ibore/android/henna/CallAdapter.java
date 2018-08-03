package ibore.android.henna;

public interface CallAdapter<T, E> {

    E adapter(Call<T> call, boolean isAsync);

}
