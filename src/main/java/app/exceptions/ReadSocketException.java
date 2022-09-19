package app.exceptions;

public class ReadSocketException extends MulticastException {
    public ReadSocketException(Throwable cause) {
        super(cause);
    }
}
