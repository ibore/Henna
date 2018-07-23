package me.ibore.http.adapter.rxjava2;

import io.reactivex.Observable;
import me.ibore.http.Call;
import me.ibore.http.CallAdapter;
import me.ibore.http.Response;


public class RxJava2CallAdapter<T> implements CallAdapter<T, Observable<Response<T>>> {

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
