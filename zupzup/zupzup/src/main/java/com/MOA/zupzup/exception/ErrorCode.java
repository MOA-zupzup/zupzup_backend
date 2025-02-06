package com.MOA.zupzup.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 편지 관련 예외
    LETTTER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디 값을 가진 편지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
