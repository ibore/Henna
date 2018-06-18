package me.ibore.http;

import android.content.DialogInterface;

import io.reactivex.observers.DisposableObserver;


/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:03
 * website: ibore.me
 */

public abstract class HttpObserver<T> extends DisposableObserver<T> implements DialogInterface.OnCancelListener {

    @Override
    protected void onStart() {


    }

    @Override
    public void onComplete() {


    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (!isDisposed()) dispose();
    }

}
