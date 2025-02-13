package com.MOA.zupzup.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LetterException.class)
    public ResponseEntity<ErrorResponse> handleLetterException(LetterException ex){
        log.error("LetterException : {}", ex.getErrorCode());
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        log.error(ex.getMessage(), ex);
        // TODO: body 필요
        return ResponseEntity.internalServerError().build();
    }
}
