package dev.jlynx.magicaldrones.exception;

public record ErrorResponse(int code, String message, String cause) {
}
