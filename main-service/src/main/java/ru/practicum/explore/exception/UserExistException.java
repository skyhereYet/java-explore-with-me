package ru.practicum.explore.exception;

public class UserExistException extends RuntimeException {
    public UserExistException(String message) {
        super(message);
    }
}
