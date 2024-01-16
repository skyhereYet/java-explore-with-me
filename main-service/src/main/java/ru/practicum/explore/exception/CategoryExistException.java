package ru.practicum.explore.exception;

public class CategoryExistException extends RuntimeException {
    public CategoryExistException(String message) {
        super(message);
    }
}
