package app.exceptions;

public class ParseConfigException extends Exception {
    public ParseConfigException(String message) {
        super(message);
    }

    public ParseConfigException(Throwable cause) {
        super(cause);
    }

}
