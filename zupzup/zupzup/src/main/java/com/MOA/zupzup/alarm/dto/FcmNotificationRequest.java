package com.MOA.zupzup.alarm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmNotificationRequest {
    private String userId;
    private String title;
    private String body;
}