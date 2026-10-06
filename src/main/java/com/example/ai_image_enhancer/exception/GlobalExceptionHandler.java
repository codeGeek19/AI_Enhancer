package com.example.ai_image_enhancer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(
            InvalidImageException.class
    )
    public ResponseEntity<Map<String, String>>
    handleInvalidImage(
            InvalidImageException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "error",
                                exception.getMessage()
                        )
                );
    }


    @ExceptionHandler(
            AIServiceException.class
    )
    public ResponseEntity<Map<String, String>>
    handleAIServiceException(
            AIServiceException exception) {

        exception.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        Map.of(
                                "error",
                                exception.getMessage()
                        )
                );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>>
    handleGeneralException(
            Exception exception) {

        exception.printStackTrace();

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        Map.of(
                                "error",
                                exception.getMessage() != null
                                        ? exception.getMessage()
                                        : "Something went wrong"
                        )
                );
    }
}