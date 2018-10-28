package me.ibore.henna.adapter.rxjava2;


import me.ibore.henna.Call;
import me.ibore.henna.HennaListener;
import me.ibore.henna.Response;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;


public class CallEnqueueObservable<T> extends Observable<Response<T>> {

    private final Call<T> originalCall;

    public CallEnqueueObservable(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    protected void subscribeActual(Observer<? super Response<T>> observer) {
        Call<T> call = originalCall.clone();
        CallListener<T> callback = new CallListener<>(call, observer);
        observer.onSubscribe(callback);
        call.enqueue(callback);
    }

    private static final class CallListener<T> implements Disposable, HennaListener<T> {

        private final Call<T> call;
        private final Observer<? super Response<T>> observer;
        boolean terminated = false;

        public CallListener(Call<T> call, Observer<? super Response<T>> observer) {
            this.call = call;
            this.observer = observer;
        }

        @Override
        public void dispose() {
            call.cancel();
        }

        @Override
        public boolean isDisposed() {
            return call.isCanceled();
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (call.isCanceled()) return;
            try {
                observer.onNext(response);
                observer.onComplete();
            } catch (Exception e) {
                if (terminated) {
                    RxJavaPlugins.onError(e);
                } else {
                    onFailure(call, e);
                }
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable e) {
            if (call.isCanceled()) return;
            try {
                terminated = true;
                observer.onError(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                RxJavaPlugins.onError(new CompositeException(e, inner));
            }
        }
    }

}
