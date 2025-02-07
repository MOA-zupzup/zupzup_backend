package com.MOA.zupzup.letter.dto;

import com.MOA.zupzup.letter.Letter;
import com.MOA.zupzup.letter.vo.LetterStatus;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;

public record DroppingLetterRequest(
        String title,
        String content,
        GeoPoint location,
        String pictureUrl,
        String paperUrl,
        String senderId
) {
    public Letter toDropLetterEntity(){
        return Letter.builder()
                .title(title)
                .content(content)
                .location(location)
                .createdAt(Timestamp.now())
                .status(LetterStatus.UNPICKED.toString())
                .pictureUrl(pictureUrl)
                .paperUrl(paperUrl)
                .senderId(senderId)
                .build();
    }
}
