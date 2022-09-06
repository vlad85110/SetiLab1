package app.exceptions;

public class MulticastException extends Exception {
    public MulticastException() {
    }

    public MulticastException(String message) {
        super(message);
    }

    public MulticastException(String message, Throwable cause) {
        super(message, cause);
    }

    public MulticastException(Throwable cause) {
        super(cause);
    }

    public MulticastException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
