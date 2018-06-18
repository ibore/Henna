package me.ibore.http.exception;

/**
 * description:
 * author: Ibore Xie
 * date: 2018-01-19 00:00
 * website: ibore.me
 */

public class HttpException extends Exception {

    private long id;

    public HttpException(String detailMessage) {
        super(detailMessage);
        this.id = -1;
    }

    public HttpException(long id, Throwable cause) {
        super(cause);
        this.id = id;
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public long getId() {
        return id;
    }

}

