package com.MOA.zupzup.letter;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.GeoPoint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Letter {
    private String id;
    private String title; // 편지 제목
    private String content; // 내용
    private GeoPoint location; // 위치
    private Timestamp createdAt; // 편지 쓴 시간
    private String status; // 상태(WRITTEN, UNWRITTEN, UNPICKED)
    private String pictureUrl; // 사진 첨부 url
    private String senderId; // 남긴 사람 url
    private String receiverId; // 받은 사람 url
}
