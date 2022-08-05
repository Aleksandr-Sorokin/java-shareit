package ru.practicum.shareit.exeptions.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exeptions.DuplicateEmail;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.exeptions.model.ErrorResponse;


@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e){
        return new ErrorResponse(String.format("Message: "
                + e.getMessage() + "\nStackTrace: " + e.getStackTrace().toString()));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e){
        return new ErrorResponse(String.format("Message: " + e.getMessage()
                + "\nStackTrace: " + e.getStackTrace().toString()));
    }

    @ExceptionHandler(DuplicateEmail.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(final DuplicateEmail e){
        return new ErrorResponse(String.format("Message: " + e.getMessage()
                + "\nStackTrace: " + e.getStackTrace().toString()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(final MethodArgumentNotValidException e){
        return new ErrorResponse("Некоректные данные " + e.getStackTrace().toString());
    }
}
