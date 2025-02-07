package com.MOA.zupzup.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 편지 관련 예외
    LETTTER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디 값을 가진 편지를 찾을 수 없습니다."),
    LETTER_CREATE_FAILED(HttpStatus.BAD_REQUEST, "편지를 생성하는데 실패했습니다."),
    LETTER_FIND_FAILED(HttpStatus.BAD_REQUEST, "편지를 불러오는데 실패했습니다."),
    LETTER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "편지를 수정하는데 실패했습니다."),
    LETTER_DELETE_FAILED(HttpStatus.BAD_REQUEST, "편지를 삭제하는데 실패했습니다."),
    FAIL_TO_FIND_MY_LETTERS(HttpStatus.BAD_REQUEST, "내 편지 리스트를 불러올 수 없습니다.")

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
