package dev.jlynx.magicaldrones.exception;

import dev.jlynx.magicaldrones.storage.StorageService;

/**
 * An unchecked exception thrown when an object requested from {@link StorageService} doesn't exist.
 */
public class NoSuchKeyStorageException extends StorageException {

    public NoSuchKeyStorageException() {
    }

    public NoSuchKeyStorageException(String message) {
        super(message);
    }

    public NoSuchKeyStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
