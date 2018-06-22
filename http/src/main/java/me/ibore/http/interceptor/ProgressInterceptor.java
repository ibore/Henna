package me.ibore.http.interceptor;

import android.os.Handler;

import java.io.IOException;

import me.ibore.http.progress.ProgressRequestBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ProgressInterceptor implements Interceptor {

    private Handler mHandler;

    public ProgressInterceptor(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request wrapRequest = originalRequest.newBuilder()
                .method(originalRequest.method(), ProgressRequestBody.create(mHandler, originalRequest.body()))
                .build();
        Request request = chain.request().newBuilder().method(chain.request().method(), chain.request().body()).build();


        chain.proceed()
        return null;
    }

}
