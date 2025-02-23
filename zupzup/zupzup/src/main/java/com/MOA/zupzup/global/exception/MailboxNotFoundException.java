package com.MOA.zupzup.global.exception;

public class MailboxNotFoundException extends RuntimeException {
    public MailboxNotFoundException(String message) {
        super(message);
    }

    public MailboxNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
