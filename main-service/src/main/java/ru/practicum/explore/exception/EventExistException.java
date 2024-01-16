package ru.practicum.explore.exception;

public class EventExistException extends RuntimeException {
    public EventExistException(String message) {
        super(message);
    }
}
