package com.MOA.zupzup.letter.dto;

import com.MOA.zupzup.letter.Letter;
import com.MOA.zupzup.letter.vo.LetterStatus;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;
import lombok.Getter;

public record DroppingLetterRequest(
        String title,
        String content,
        GeoPoint location,
        String pictureUrl,
        String paperUrl,
        String senderId,
        String mailboxId,
        String stationeryId
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
                .mailboxId(mailboxId)
                .stationeryId(stationeryId)
                .build();
    }

    public String getMailboxId(){
        return mailboxId;
    }
}
