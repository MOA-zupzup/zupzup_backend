package com.MOA.zupzup.letter.dto;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;

public record UnpickedLetterResponse(
        String id,
        String title,
        String content,
        Timestamp createdAt,
        String pictureUrl,
        String paperUrl,
        String senderId
) {
}
