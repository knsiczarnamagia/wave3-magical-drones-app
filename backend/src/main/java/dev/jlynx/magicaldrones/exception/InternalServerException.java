package dev.jlynx.magicaldrones.exception;

/**
 * A generic exception thrown with in situations where INTERNAL SERVER ERROR status code is appropriate.
 */
public class InternalServerException extends RuntimeException {

    public InternalServerException() {
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
