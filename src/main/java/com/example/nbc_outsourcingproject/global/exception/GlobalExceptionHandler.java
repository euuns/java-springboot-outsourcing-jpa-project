package com.example.nbc_outsourcingproject.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OutsourcingException.class)
    public ResponseEntity<ErrorResponse> handleMenuException(OutsourcingException e) {
        return new ResponseEntity<>(ErrorResponse.of(e.getErrorCode().getName(), e.getHttpStatus(), e.getMessage()), e.getHttpStatus());
    }
}

