package com.MOA.zupzup.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LetterException extends RuntimeException{
    private final ErrorCode errorCode;
}
