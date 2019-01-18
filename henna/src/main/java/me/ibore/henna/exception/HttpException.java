package me.ibore.henna.exception;


import me.ibore.henna.HennaUtils;


/**
 * description: HTTP状态异常
 * author: Ibore Xie
 * date: 2018-01-19
 * website: ibore.me
 */
public final class HttpException extends RuntimeException {

    private static String getMessage(okhttp3.Response response) {
        HennaUtils.checkNotNull(response, "response == null");
        return "HTTP " + response.code() + " " + response.message();
    }

    private final int code;
    private final String message;
    private final transient okhttp3.Response response;

    public HttpException(okhttp3.Response response) {
        super(getMessage(response));
        this.code = response.code();
        this.message = response.message();
        this.response = response;
    }

    /**
     * HTTP status code.
     */
    public int code() {
        return code;
    }

    /**
     * HTTP status message.
     */
    public String message() {
        return message;
    }

    /**
     * The full HTTP response. This may be null if the exception was serialized.
     */
    public okhttp3.Response response() {
        return response;
    }

}

