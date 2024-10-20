package com.example.tinkoff.utilities;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class ControllersAdvice {
    public record ErrorMessage(String message, int code) {}

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = "";
        if (Objects.equals(e.getParameter().getParameterName(), "amount"))
            message = "Amount must be positive integer";
        else
            message = e.getParameter().getParameterName() + "must not be null or empty";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content type", "application/xml");
        return new ResponseEntity<>(
                new ErrorMessage(message, HttpStatus.BAD_REQUEST.value()),
                headers,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({CurrencyNotExistException.class})
    public ResponseEntity<ErrorMessage> handleCurrencyNotExistException(CurrencyNotExistException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content type", "application/xml");
        return new ResponseEntity<>(
                new ErrorMessage(String.format("Currency with code '{0}' is not existed", e.getIsoCharCode()), HttpStatus.BAD_REQUEST.value()),
                headers,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({CurrencyNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleCurrencyNotFoundException(CurrencyNotExistException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content type", "application/xml");
        return new ResponseEntity<>(
                new ErrorMessage(String.format("Currency with code '{0}' is not found", e.getIsoCharCode()), HttpStatus.NOT_FOUND.value()),
                headers,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ServiceUnavailableException.class})
    public ResponseEntity<ErrorMessage> handleServiceUnavailableException(ServiceUnavailableException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", "3600");
        return new ResponseEntity<>(
                new ErrorMessage(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()),
                headers,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}
