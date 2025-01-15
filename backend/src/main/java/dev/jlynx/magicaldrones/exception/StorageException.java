package dev.jlynx.magicaldrones.exception;

import dev.jlynx.magicaldrones.storage.StorageService;

/**
 * An unchecked exception thrown when an I/O error has occurred while interacting with a {@link StorageService}.
 */
public class StorageException extends RuntimeException {

    public StorageException() {
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
