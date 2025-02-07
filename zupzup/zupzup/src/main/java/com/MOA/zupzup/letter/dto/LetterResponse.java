package com.MOA.zupzup.letter.dto;

import com.MOA.zupzup.letter.Letter;
import com.google.cloud.Timestamp;

public record LetterResponse(
        String id,
        String title,
        String content,
        Timestamp createdAt,
        String pictureUrl,
        String paperUrl,
        String senderId,
        String receiverId
) {
    public static LetterResponse from(Letter letter){
        return new LetterResponse(
                letter.getId(),
                letter.getTitle(),
                letter.getContent(),
                letter.getCreatedAt(),
                letter.getPictureUrl(),
                letter.getPaperUrl(),
                letter.getSenderId(),
                letter.getReceiverId()
        );
    }
}
