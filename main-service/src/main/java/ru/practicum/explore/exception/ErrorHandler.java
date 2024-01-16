package ru.practicum.explore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.warn("Error caught: 400, " + e.getMessage());
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("Error caught: 400, " + e.getMessage());
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserExistException(final UserExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(
                                    HttpStatus.NOT_FOUND,
                                    "The required object was not found.",
                                    e.getMessage(),
                                    LocalDateTime.now(),
                                    e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCategoryExistException(final CategoryExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(
                                    HttpStatus.NOT_FOUND,
                                    "The required object was not found.",
                                    e.getMessage(),
                                    LocalDateTime.now(),
                                    e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestExistException(final RequestExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(
                                    HttpStatus.NOT_FOUND,
                                    "The required object was not found.",
                                    e.getMessage(),
                                    LocalDateTime.now(),
                                    e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEventExistException(final EventExistException e) {
        log.warn("Error caught: 404, " + e.getMessage());
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now(),
                e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestInvalidException(final RequestInvalidException e) {
        log.warn("Error caught: 400, " + e.getMessage());
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                LocalDateTime.now(),
                e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictRuleException(ConflictRuleException e) {
        log.warn("Error caught: 409, ", e);
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExceptionValidate(DataIntegrityViolationException e) {
        log.warn("Error caught: 409, ", e);
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStateEventException(StateEventException e) {
        log.warn("Error caught: 404, ", e);
        return new ErrorResponse(HttpStatus.NOT_FOUND, "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now(), e.getStackTrace().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCompilationExistException(CompilationExistException e) {
        log.warn("Error caught: 404, ", e);
        return new ErrorResponse(HttpStatus.NOT_FOUND, "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now(), e.getStackTrace().toString());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders httpHeaders, HttpStatus httpStatus,
                                                                  WebRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", httpStatus);
        body.put("reason", "Incorrectly made request.");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(body, httpHeaders, httpStatus);
    }
}