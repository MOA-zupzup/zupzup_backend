package com.MOA.zupzup.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopException extends RuntimeException {
    private final ErrorCode errorCode;
}
