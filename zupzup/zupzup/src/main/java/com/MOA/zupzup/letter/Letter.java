package com.MOA.zupzup.letter;

import com.MOA.zupzup.letter.vo.LetterStatus;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter {
    private String id;
    private String title; // 편지 제목
    private String content; // 내용
    private GeoPoint location; // 위치
    private Timestamp createdAt; // 편지 쓴 시간
    private String status; // 상태(WRITTEN, UNWRITTEN, UNPICKED)
    private String pictureUrl; // 사진 첨부 url
    private String paperUrl; // 편지지 url
    private String senderId; // 남긴 사람
    private String receiverId; // 받은 사람

    public Letter(String id, String title, String content, GeoPoint location, Timestamp createdAt, String status, String pictureUrl, String paperUrl, String senderId, String receiverId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.location = location;
        this.createdAt = createdAt;
        this.status = status;
        this.pictureUrl = pictureUrl;
        this.paperUrl = paperUrl;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void pickUp(String receiverId) {
        this.status = LetterStatus.WRITTEN.toString();
        this.receiverId = receiverId;
    }
}
