package ru.practicum.explore.exception;

public class RequestInvalidException extends RuntimeException {

    public RequestInvalidException(String message) {
        super(message);
    }
}
