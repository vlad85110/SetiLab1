package app.exceptions;

public class ParseJsonException extends MulticastException {
    public ParseJsonException(Throwable cause) {
        super(cause);
    }
}
