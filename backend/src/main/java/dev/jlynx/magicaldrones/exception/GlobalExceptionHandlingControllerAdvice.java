package dev.jlynx.magicaldrones.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandlingControllerAdvice.class);

    @ExceptionHandler({ ResourceNotFoundException.class, NoSuchKeyStorageException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(Exception ex) {
        log.trace("A NOT_FOUND exception handler invoked by: {}", ex.toString());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
                ex.getCause() != null ? ex.getCause().toString() : null);
    }

    @ExceptionHandler({ ConstraintViolationException.class, MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(Exception ex) {
        log.trace("A BAD_REQUEST exception handler invoked by: {}", ex.toString());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getCause() != null ? ex.getCause().toString() : null);
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(AccessForbiddenException ex) {
        log.trace("A FORBIDDEN exception handler invoked by: {}", ex.toString());
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(),
                ex.getCause() != null ? ex.getCause().toString() : null);
    }

    @ExceptionHandler(UsernameExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(UsernameExistsException ex) {
        log.trace("A CONFLICT exception handler invoked by: {}", ex.toString());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(),
                ex.getCause() != null ? ex.getCause().toString() : null);
    }

    @ExceptionHandler({ StorageException.class, ImageServiceException.class, InternalServerException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(Exception ex) {
        log.trace("An INTERNAL_SERVER_ERROR exception handler invoked by: {}", ex.toString());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
                ex.getCause() != null ? ex.getCause().toString() : null);
    }
}
