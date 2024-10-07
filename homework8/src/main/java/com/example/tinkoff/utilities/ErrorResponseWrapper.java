package com.example.tinkoff.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorResponseWrapper {
    public record ErrorMessage(String message, int code) {}

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity<ErrorMessage> handleJsonProcessingException(JsonProcessingException e) {
        return new ResponseEntity<>(
            new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(
            new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }
}
