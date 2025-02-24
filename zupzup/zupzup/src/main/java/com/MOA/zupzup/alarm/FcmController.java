package com.MOA.zupzup.alarm;

import com.MOA.zupzup.alarm.dto.FcmNotificationRequest;
import com.MOA.zupzup.alarm.dto.FcmTokenRequest;
import com.MOA.zupzup.login.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/fcm")
@Tag(name = "FCM", description = "Firebase Cloud Messaging 관련 API")
public class FcmController {

    private final FirebaseMessagingService firebaseMessagingService;

    @Autowired
    public FcmController(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @Operation(summary = "FCM 토큰 저장 또는 업데이트", description = "사용자의 FCM 토큰을 저장하거나 업데이트합니다.")
    @PostMapping("/token")
    public ApiResponse<Void> saveOrUpdateToken(@RequestBody FcmTokenRequest request) {
        try {
            firebaseMessagingService.saveOrUpdateToken(request.getUserId(), request.getToken());
            return new ApiResponse<>(true, "Token saved or updated successfully", null);
        } catch (ExecutionException | InterruptedException e) {
            return new ApiResponse<>(false, "Failed to save or update token", null);
        }
    }

    @Operation(summary = "사용자에게 알림 전송", description = "특정 사용자에게 FCM 알림을 전송합니다.")
    @PostMapping("/sendToUser")
    public ApiResponse<Void> sendNotificationToUser(@RequestBody FcmNotificationRequest request) {
        try {
            firebaseMessagingService.sendNotificationToUser(request.getUserId(), request.getTitle(), request.getBody());
            return new ApiResponse<>(true, "Notification sent successfully", null);
        } catch (ExecutionException | InterruptedException e) {
            return new ApiResponse<>(false, "Failed to send notification", null);
        }
    }
}