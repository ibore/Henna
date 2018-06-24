package me.ibore.http.exception;

/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:00
 * website: ibore.me
 */

public class HttpException extends Exception {

    private long code;

    public HttpException(String detailMessage) {
        super(detailMessage);
        this.code = -1;
    }

    public HttpException(int code, String detailMessage) {
        super(detailMessage);
        this.code = code;
    }

    public HttpException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public HttpException(Throwable cause) {
        super(cause);
        this.code = -1;
    }

    public long getCode() {
        return code;
    }

}

