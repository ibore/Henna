package me.ibore.http.call;

import java.io.IOException;

import me.ibore.http.Response;
import me.ibore.http.exception.HttpException;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.progress.ProgressResponseBody;
import me.ibore.http.request.Request;
import me.ibore.http.utils.HttpUtils;

public class OkHttpCall<T> implements Call<T> {

    private Request<T, ? extends Request> request;

    public OkHttpCall(Request<T, ? extends Request> request) {
        this.request = request;
    }

    @Override
    public Response<T> execute() throws Exception {
        okhttp3.Request rawRequest = new okhttp3.Request.Builder()
                .method(request.getMethod(), HttpUtils.generateMultipartRequestBody())
                .build();
        okhttp3.Response rawResponse = request.getClient().newCall(request.removeAllHeaders()).execute();
        if (rawResponse.isSuccessful()) {
            if (null != request.downloadListener()) {
                rawResponse = rawResponse.newBuilder().body(ProgressResponseBody.create(rawResponse.body(), request.downloadListener(), refreshTime)).build();
            }
            if (null == request.converter()) throw new NullPointerException("converter can not be null");
            return Response.success(rawResponse, request.converter().convert(rawResponse.body()));
        } else {
            throw new HttpException(rawResponse.code(), rawResponse.message());
        }
    }


    @Override
    public void enqueue(HennaListener<T> listener) {
        if (null == listener) throw new NullPointerException("HennaListener can not be null");
        request.henna().runOnUiThread(listener::onStart);
        okhttp3.Request.Builder builder = generateRequest(listener);
        client.newCall(builder.build()).enqueue(new okhttp3.Callback() {

            int retryCount = 0;

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (retryCount < request.maxRetry) {
                    client.newCall(builder.build()).enqueue(this);
                    retryCount++;
                } else {
                    henna.runOnUiThread(() -> listener.onError(new HttpException(-1, e.getMessage())));
                    henna.runOnUiThread(listener::onFinish);
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) throws IOException {
                if (rawResponse.isSuccessful()) {
                    try {
                        if (null != downloadListener) {
                            rawResponse = rawResponse.newBuilder().body(ProgressResponseBody.create(rawResponse.body(), downloadListener, refreshTime)).build();
                        }
                        if (null == converter)
                            throw new NullPointerException("converter can not be null");
                        T object = converter.convert(rawResponse.body());
                        henna.runOnUiThread(() -> listener.onResponse(call, object));
                    } catch (Exception e) {
                        henna.runOnUiThread(() -> listener.onError(new HttpException(-1, e.getMessage())));
                    }
                } else {
                    Response finalResponse = response;
                    henna.runOnUiThread(() -> listener.onError(new HttpException(finalResponse.code(), finalResponse.message())));
                }
                henna.runOnUiThread(listener::onFinish);
            }

        });
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request<T> request() {
        return request;
    }
}
