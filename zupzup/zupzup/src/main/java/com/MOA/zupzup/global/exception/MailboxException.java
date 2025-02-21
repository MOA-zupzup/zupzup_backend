package com.MOA.zupzup.global.exception;


public class MailboxException extends RuntimeException {
    private final ErrorCode errorCode;

    public MailboxException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}