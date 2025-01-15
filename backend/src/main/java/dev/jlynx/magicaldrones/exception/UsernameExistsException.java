package dev.jlynx.magicaldrones.exception;

public class UsernameExistsException extends RuntimeException {

    public UsernameExistsException() {
    }

    public UsernameExistsException(String message) {
        super(message);
    }
}
