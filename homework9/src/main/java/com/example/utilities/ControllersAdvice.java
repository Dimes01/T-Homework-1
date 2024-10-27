package com.example.utilities;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;


@ControllerAdvice
public class ControllersAdvice {
    public record ErrorMessage(String message, int code) {}

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class, RestClientResponseException.class})
    public ResponseEntity<ErrorMessage> handleCurrencyNotExistException(Exception e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", "3600");
        return new ResponseEntity<>(
                new ErrorMessage(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()),
                headers,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message;
        if (Objects.equals(e.getParameter().getParameterName(), "amount"))
            message = "Amount must be positive integer";
        else
            message = e.getParameter().getParameterName() + "must not be null or empty";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content type", "application/json");
        return new ResponseEntity<>(
                new ErrorMessage(message, HttpStatus.BAD_REQUEST.value()),
                headers,
                HttpStatus.BAD_REQUEST
        );
    }
}
