package me.ibore.henna.adapter.rxjava2;

import me.ibore.henna.Call;
import me.ibore.henna.CallAdapter;
import me.ibore.henna.Response;
import io.reactivex.Observable;


public class RxJava2CallAdapter<T> implements CallAdapter<T, Observable<Response<T>>> {

    public static RxJava2CallAdapter create() {
        return new RxJava2CallAdapter();
    }

    @Override
    public Observable<Response<T>> adapter(Call<T> call, boolean isAsync) {
        Observable<Response<T>> observable;
        if (isAsync) {
            observable = new CallEnqueueObservable<>(call);
        } else {
            observable = new CallExecuteObservable<>(call);
        }
        return observable;
    }
}
