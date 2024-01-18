package ru.practicum.explore.exception;

public class RequestExistException extends RuntimeException {

    public RequestExistException(String message) {
        super(message);
    }
}
