package me.ibore.henna;

import android.support.annotation.Nullable;

import okhttp3.Headers;

public final class Response<T> {

    private final okhttp3.Response rawResponse;
    private final T body;

    private Response(okhttp3.Response rawResponse, T body) {
        this.rawResponse = rawResponse;
        this.body = body;
    }

    public okhttp3.Response raw() {
        return rawResponse;
    }

    public int code() {
        return rawResponse.code();
    }

    public String message() {
        return rawResponse.message();
    }

    public Headers headers() {
        return rawResponse.headers();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public @Nullable T body() {
        return body;
    }

    @Override
    public String toString() {
        return rawResponse.toString();
    }

    public static <T> Response<T> success(okhttp3.Response rawResponse, T body) {
        return new Response<>(rawResponse, body);
    }
}
