package app.exceptions;

public class ReadSocketException extends MulticastException {
    public ReadSocketException() {
    }

    public ReadSocketException(String message) {
        super(message);
    }

    public ReadSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadSocketException(Throwable cause) {
        super(cause);
    }

    public ReadSocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
