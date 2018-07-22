package me.ibore.http.call;

import android.support.annotation.NonNull;

import java.io.IOException;

import me.ibore.http.Response;
import me.ibore.http.exception.HttpException;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.progress.ProgressResponseBody;
import me.ibore.http.request.Request;
import me.ibore.http.utils.HttpUtils;

public class OkHttpCall<T> implements Call<T> {

    private okhttp3.Call rawCall;
    private Request<T, ? extends Request> request;

    public OkHttpCall(Request<T, ? extends Request> request) {
        this.request = request;
        rawCall = request.getRawCall();
    }

    @Override
    public Response<T> execute() throws Exception {
        okhttp3.Response rawResponse = rawCall.execute();
        if (rawResponse.isSuccessful()) {
            if (null != request.getDownloadListener()) {
                rawResponse = rawResponse.newBuilder().body(ProgressResponseBody.create(rawResponse.body(), request.getDownloadListener(), request.getRefreshTime())).build();
            }
            if (null == request.getConverter()) throw new NullPointerException("converter can not be null");
            return Response.success(rawResponse, request.getConverter().convert(rawResponse.body()));
        } else {
            throw new HttpException(rawResponse.code(), rawResponse.message());
        }
    }


    @Override
    public void enqueue(HennaListener<T> listener) {
        if (null == listener) throw new NullPointerException("HennaListener can not be null");
        if (request.isUIThread()) {
            HttpUtils.runOnUiThread(() -> listener.onStart(request));
        } else {
            listener.onStart(request);
        }

        rawCall.enqueue(new okhttp3.Callback() {

            int retryCount = 0;

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                if (retryCount < request.getMaxRetry()) {
                    call.clone().enqueue(this);
                    retryCount++;
                } else {
                    if (request.isUIThread()) {
                        HttpUtils.runOnUiThread(() -> {
                            listener.onFailure(OkHttpCall.this, e);
                            listener.onFinish();
                        });
                    } else {
                        listener.onFailure(OkHttpCall.this, e);
                        listener.onFinish();
                    }
                }
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response rawResponse) throws IOException {
                if (rawResponse.isSuccessful()) {
                    try {
                        if (null != request.getDownloadListener()) {
                            rawResponse = rawResponse.newBuilder().body(
                                    ProgressResponseBody.create(rawResponse.body(),
                                            request.getDownloadListener(),
                                            request.getRefreshTime())).build();
                        }
                        if (null == request.getConverter())
                            throw new NullPointerException("converter can not be null");
                        T object = request.getConverter().convert(rawResponse.body());
                        if (request.isUIThread()) {
                            okhttp3.Response finalRawResponse = rawResponse;
                            HttpUtils.runOnUiThread(() -> {
                                listener.onResponse(OkHttpCall.this, Response.success(finalRawResponse, object));
                                listener.onFinish();
                            });
                        }
                    } catch (Exception e) {
                        if (e instanceof IOException) {
                            throw e;
                        } else {
                            if (request.isUIThread()) {
                                HttpUtils.runOnUiThread(() -> {
                                    listener.onFailure(OkHttpCall.this, e);
                                    listener.onFinish();
                                });
                            } else {
                                listener.onFailure(OkHttpCall.this, e);
                                listener.onFinish();
                            }
                        }
                    }
                } else {
                    if (request.isUIThread()) {
                        okhttp3.Response finalRawResponse1 = rawResponse;
                        HttpUtils.runOnUiThread(() -> {
                            listener.onFailure(OkHttpCall.this, new HttpException(finalRawResponse1.code(), finalRawResponse1.message()));
                            listener.onFinish();
                        });
                    } else {
                        listener.onFailure(OkHttpCall.this, new HttpException(rawResponse.code(), rawResponse.message()));
                        listener.onFinish();
                    }
                }
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return rawCall.isExecuted();
    }

    @Override
    public void cancel() {
        rawCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return rawCall.isCanceled();
    }

    @Override
    public Call<T> clone() {
        return new OkHttpCall<>(request);
    }

    @Override
    public Request<T, ? extends Request> request() {
        return request;
    }
}
