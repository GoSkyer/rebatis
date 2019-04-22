package org.gosky.exception;

/**
 * @Auther: guozhong
 * @Date: 2019-04-22 14:31
 * @Description:
 */
public class ConvertReturnTypeException extends RuntimeException {

    public ConvertReturnTypeException() {
    }

    public ConvertReturnTypeException(String message) {
        super(message);
    }

    public ConvertReturnTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertReturnTypeException(Throwable cause) {
        super(cause);
    }

    public ConvertReturnTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
