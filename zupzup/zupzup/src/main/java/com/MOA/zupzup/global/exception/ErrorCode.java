package com.MOA.zupzup.global.exception;

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
    FAIL_TO_FIND_MY_LETTERS(HttpStatus.BAD_REQUEST, "내 편지 리스트를 불러올 수 없습니다."),

    // 우편함 관련 예외들
    MAILBOX_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디 값을 가진 우편함을 찾을 수 없습니다."),
    MAILBOX_CREATE_FAILED(HttpStatus.BAD_REQUEST, "우편함 생성에 실패했습니다."),
    MAILBOX_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 우편함입니다."),
    MAILBOX_FIND_FAILED(HttpStatus.BAD_REQUEST, "우편함 조회에 실패했습니다."),
    MAILBOX_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "우편함 업데이트에 실패했습니다."),
    MAILBOX_DELETE_FAILED(HttpStatus.BAD_REQUEST, "우편함 삭제에 실패했습니다."),
    MAILBOX_ID_MISMATCH(HttpStatus.BAD_REQUEST, "경로 변수의 ID와 요청 본문의 ID가 일치하지 않습니다."),
    MAILBOX_ID_REQUIRED(HttpStatus.BAD_REQUEST, "우편함 ID는 필수 값입니다."),
    NO_MAILBOXES_FOUND(HttpStatus.NOT_FOUND, "등록된 우편함이 존재하지 않습니다."),
    INVALID_USER_LOCATION(HttpStatus.BAD_REQUEST, "잘못된 사용자 위치 데이터입니다."),
    INVALID_MAILBOX_LOCATION(HttpStatus.BAD_REQUEST, "잘못된 우편함 위치 데이터입니다."),
    LETTER_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 우편함에 이미 존재하는 편지입니다."),
    LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 우편함에서 편지를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}