package me.ibore.henna;

import android.support.annotation.NonNull;

import java.io.IOException;

public class OkHttpCall<T> implements Call<T> {

    private okhttp3.Call rawCall;
    private Request<T, ? extends Request> request;

    public OkHttpCall(Request<T, ? extends Request> request) {
        this.request = request;
        rawCall = request.getClient().newCall(request.generateRequest());
    }

    @Override
    public Response<T> execute() throws Exception {
        okhttp3.Response rawResponse = rawCall.execute();
        if (rawResponse.isSuccessful()) {
            if (null != request.getDownloadListener()) {
                rawResponse = rawResponse.newBuilder().body(ProgressResponseBody.create(rawResponse.body(),
                        request.getDownloadListener(), request.isUIThread(), request.getRefreshTime())).build();
            }
            if (null == request.getConverter()) throw new NullPointerException("converter can not be null");
            return Response.success(rawResponse, request.getConverter().convert(rawResponse));
        } else {
            throw new HttpException(rawResponse.code(), rawResponse.message());
        }
    }


    @Override
    public void enqueue(final HennaListener<T> listener) {
        if (null == listener) throw new NullPointerException("HennaListener can not be null");
        if (request.isUIThread()) {
            HennaUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onStart(request);
                }
            });
        } else {
            listener.onStart(request);
        }

        rawCall.enqueue(new okhttp3.Callback() {

            int retryCount = 0;

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull final IOException e) {
                if (retryCount < request.getMaxRetry()) {
                    call.clone().enqueue(this);
                    retryCount++;
                } else {
                    if (request.isUIThread()) {
                        HennaUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailure(OkHttpCall.this, e);
                            }
                        });
                    } else {
                        listener.onFailure(OkHttpCall.this, e);
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
                                            request.getDownloadListener(), request.isUIThread(),
                                            request.getRefreshTime())).build();
                        }
                        final T object = request.getConverter().convert(rawResponse);
                        if (request.isUIThread()) {
                            final okhttp3.Response finalRawResponse = rawResponse;
                            HennaUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onResponse(OkHttpCall.this, Response.success(finalRawResponse, object));
                                    listener.onFinish();
                                }
                            });
                        } else {
                            listener.onResponse(OkHttpCall.this, Response.success(rawResponse, object));
                            listener.onFinish();
                        }
                    } catch (final Exception e) {
                        if (e instanceof IOException) {
                            throw e;
                        } else {
                            if (request.isUIThread()) {
                                HennaUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onFailure(OkHttpCall.this, e);
                                    }
                                });
                            } else {
                                listener.onFailure(OkHttpCall.this, e);
                            }
                        }
                    }
                } else {
                    if (request.isUIThread()) {
                        final okhttp3.Response finalRawResponse1 = rawResponse;
                        HennaUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailure(OkHttpCall.this, new HttpException(finalRawResponse1.code(), finalRawResponse1.message()));
                            }
                        });
                    } else {
                        listener.onFailure(OkHttpCall.this, new HttpException(rawResponse.code(), rawResponse.message()));
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
