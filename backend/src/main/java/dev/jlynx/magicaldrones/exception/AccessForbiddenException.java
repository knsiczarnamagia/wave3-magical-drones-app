package dev.jlynx.magicaldrones.exception;

public class AccessForbiddenException extends RuntimeException {

    public AccessForbiddenException() {
    }

    public AccessForbiddenException(String message) {
        super(message);
    }

    public AccessForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
