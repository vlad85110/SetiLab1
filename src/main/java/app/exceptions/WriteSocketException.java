package app.exceptions;

public class WriteSocketException extends MulticastException {
    public WriteSocketException() {
        super();
    }

    public WriteSocketException(String message) {
        super(message);
    }

    public WriteSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteSocketException(Throwable cause) {
        super(cause);
    }

    public WriteSocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
