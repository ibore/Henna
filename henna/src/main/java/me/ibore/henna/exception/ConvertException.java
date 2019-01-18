package me.ibore.henna.exception;

/**
 * description: 转换异常
 * author: Ibore Xie
 * date: 2018-01-19
 * website: ibore.me
 */
public final class ConvertException extends RuntimeException {

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertException(Throwable cause) {
        super(cause);
    }

}
