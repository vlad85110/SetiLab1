package app.exceptions;

public class ParseJsonException extends MulticastException {
    public ParseJsonException() {
    }

    public ParseJsonException(String message) {
        super(message);
    }

    public ParseJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseJsonException(Throwable cause) {
        super(cause);
    }

    public ParseJsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
