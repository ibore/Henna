package ibore.android.henna.adapter.rxjava2;

import ibore.android.henna.Call;
import ibore.android.henna.Response;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;


public class CallExecuteObservable<T> extends Observable<Response<T>> {

    private final Call<T> originalCall;

    public CallExecuteObservable(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    protected void subscribeActual(Observer<? super Response<T>> observer) {
        Call<T> call = originalCall.clone();
        observer.onSubscribe(new Disposable() {
            @Override
            public void dispose() {
                call.cancel();
            }

            @Override
            public boolean isDisposed() {
                return call.isCanceled();
            }
        });

        boolean terminated = false;
        try {
            Response<T> response = call.execute();
            if (!call.isCanceled()) {
                observer.onNext(response);
            }
            if (!call.isCanceled()) {
                terminated = true;
                observer.onComplete();
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            if (terminated) {
                RxJavaPlugins.onError(t);
            } else if (!call.isCanceled()) {
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }
    }
}
