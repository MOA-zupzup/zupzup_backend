package com.MOA.zupzup.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailboxException extends RuntimeException {
    private final ErrorCode errorCode;
}
