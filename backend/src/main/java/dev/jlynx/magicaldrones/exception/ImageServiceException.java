package dev.jlynx.magicaldrones.exception;

public class ImageServiceException extends RuntimeException {

    public ImageServiceException() {
    }

    public ImageServiceException(String message) {
        super(message);
    }

    public ImageServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
