package me.ibore.http.call;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;
import me.ibore.http.Response;
import me.ibore.http.exception.HttpException;
import me.ibore.http.progress.ProgressResponseBody;
import okhttp3.ResponseBody;

public class RxJavaCallAdapter<T> implements ObservableOnSubscribe<ResponseBody>, Call.Adapter<T, Observable<Response<T>>> {


    @Override
    public void subscribe(ObservableEmitter<ResponseBody> emitter) throws Exception {
        try {
            Response response = client.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                if (isProgress) {
                    if (null == listener)
                        throw new NullPointerException("ProgressListener can not be null");
                    response = response.newBuilder().body(ProgressResponseBody.create(
                            henna.getDelivery(), response.body(), listener, refreshTime)).build();
                }
                emitter.onNext(response.body());
            } else {
                emitter.onError(HttpException.newInstance(response.code()));
            }
        } catch (Exception e) {
            emitter.onError(e);
        } finally {
            emitter.onComplete();
        }
        emitter.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                henna.cancelTag(tag);
            }
        });
    }

    @Override
    public Observable<Response<T>> adapter(Call<T> call) {
        return null;
    }
}
