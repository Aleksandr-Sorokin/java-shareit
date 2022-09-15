package ru.practicum.shareit.exeptions;

public class DuplicateEmail extends RuntimeException {
    public DuplicateEmail(String message) {
        super(message);
    }
}