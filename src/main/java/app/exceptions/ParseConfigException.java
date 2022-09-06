package app.exceptions;

public class ParseConfigException extends Exception {
    public ParseConfigException() {
    }

    public ParseConfigException(String message) {
        super(message);
    }

    public ParseConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseConfigException(Throwable cause) {
        super(cause);
    }

    public ParseConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
