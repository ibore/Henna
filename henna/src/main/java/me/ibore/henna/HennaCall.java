package me.ibore.henna;

import java.io.IOException;

import me.ibore.henna.exception.ConvertException;
import me.ibore.henna.exception.HttpException;
import me.ibore.henna.progress.ProgressResponseBody;

public class HennaCall<T> implements Call<T> {

    private okhttp3.Call rawCall;
    private Request<T, ? extends Request> request;

    public HennaCall(Request<T, ? extends Request> request) {
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
            return Response.success(rawResponse, request.getConverter().convert(rawResponse));
        } else {
            throw new HttpException(rawResponse.code(), rawResponse.message());
        }
    }


    @Override
    public void enqueue(final HennaListener<T> listener) {
        if (null == listener) throw new NullPointerException("HennaListener can not be null");
        rawCall.enqueue(new okhttp3.Callback() {
            int retryCount = 0;
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                if (retryCount < request.getMaxRetry()) {
                    call.clone().enqueue(this);
                    retryCount++;
                } else {
                    if (request.isUIThread()) {
                        HennaUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailure(HennaCall.this, e);
                            }
                        });
                    } else {
                        listener.onFailure(HennaCall.this, e);
                    }
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) throws IOException {
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
                                    listener.onResponse(HennaCall.this, Response.success(finalRawResponse, object));
                                }
                            });
                        } else {
                            listener.onResponse(HennaCall.this, Response.success(rawResponse, object));
                        }
                    } catch (IOException e) {
                        throw e;
                    } catch (final ConvertException e) {
                        if (request.isUIThread()) {
                            HennaUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() { listener.onFailure(HennaCall.this, e);
                                }
                            });
                        } else {
                            listener.onFailure(HennaCall.this, e);
                        }
                    }
                } else {
                    if (request.isUIThread()) {
                        final okhttp3.Response finalRawResponse1 = rawResponse;
                        HennaUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFailure(HennaCall.this, new HttpException(finalRawResponse1.code(), finalRawResponse1.message()));
                            }
                        });
                    } else {
                        listener.onFailure(HennaCall.this, new HttpException(rawResponse.code(), rawResponse.message()));
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
        return new HennaCall<>(request);
    }

    @Override
    public Request<T, ? extends Request> request() {
        return request;
    }
}
