package com.MOA.zupzup.alarm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenRequest {
    private String userId;
    private String token;
}