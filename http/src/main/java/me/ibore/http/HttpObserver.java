package me.ibore.http;

import android.content.Context;
import android.content.DialogInterface;

import io.reactivex.observers.DisposableObserver;
import me.ibore.http.exception.HttpException;


/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:03
 * website: ibore.me
 */

public abstract class HttpObserver<T> extends DisposableObserver<T> implements DialogInterface.OnCancelListener {

    @Override
    protected void onStart() {
        if (!Utils.isNetworkConnected(XHttp.getContext())) {
            dispose();
            onError(new HttpException(HttpException.NetworkNotConnected, "网络未连接"));
        } else if (!Utils.isNetworkAvailable(XHttp.getContext())) {
            dispose();
            onError(new HttpException(HttpException.NetworkNotAvailable, "无法上网"));
        }
    }

    @Override
    public void onComplete() {


    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (!isDisposed()) dispose();
    }

}
