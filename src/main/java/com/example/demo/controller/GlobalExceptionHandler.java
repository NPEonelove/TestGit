package com.example.demo.controller;

import com.example.demo.exception.ErrorResponse;
import com.example.demo.exception.LoginAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "User not found exception",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLoginAlreadyExists(LoginAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "Login already exists",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }
}
