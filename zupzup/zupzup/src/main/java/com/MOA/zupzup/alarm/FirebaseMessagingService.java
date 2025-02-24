package com.MOA.zupzup.alarm;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseMessagingService {

    private final Firestore firestore;

    public FirebaseMessagingService(Firestore firestore) {
        this.firestore = firestore;
    }

    @Transactional
    public void saveOrUpdateToken(String userId, String token) throws ExecutionException, InterruptedException {
        CollectionReference tokensRef = firestore.collection("fcmTokens");
        DocumentReference tokenDoc = tokensRef.document(userId);
        ApiFuture<DocumentSnapshot> future = tokenDoc.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            tokenDoc.update("token", token);
        } else {
            tokenDoc.set(new FcmToken(userId, token));
        }
    }

    public boolean isTokenValid(String token) {
        try {
            FirebaseMessaging.getInstance().send(
                    Message.builder().setToken(token).build()
            );
            return true;
        } catch (FirebaseMessagingException e) {
            return false;
        }
    }

    public String sendNotification(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            return e.getMessagingErrorCode().toString();
        }
    }

    @Transactional
    public void sendNotificationToUser(String userId, String title, String body) throws ExecutionException, InterruptedException {
        DocumentReference tokenDoc = firestore.collection("fcmTokens").document(userId);
        ApiFuture<DocumentSnapshot> future = tokenDoc.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            String token = document.getString("token");
            if (isTokenValid(token)) {
                sendNotification(token, title, body);
            }
        }
    }
}