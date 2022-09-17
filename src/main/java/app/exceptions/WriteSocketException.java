package app.exceptions;

public class WriteSocketException extends MulticastException {
    public WriteSocketException(Throwable cause) {
        super(cause);
    }
}
