package com.MOA.zupzup.mailbox;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class MailboxService {

    private static final String COLLECTION_NAME = "mailboxes";

    private CollectionReference getMailboxCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_NAME);
    }

    public String createMailbox(Mailbox mailbox, String mailboxId) {
        DocumentReference docRef = getMailboxCollection().document(mailboxId);
        mailbox.setId(mailboxId);
        mailbox.setLetterCount(mailbox.getLetterIds().size());
        ApiFuture<WriteResult> mailboxApiFuture = docRef.set(mailbox);
        try {
            mailboxApiFuture.get(); // Wait for the operation to complete
        } catch (Exception e) {
            // 예외 처리 부분을 제거했으므로, 그냥 출력하고 종료하도록 할 수 있습니다.
            e.printStackTrace(); // 또는 로그를 남길 수 있습니다.
        }
        return docRef.getId();
    }

    public Mailbox findMailboxById(String id) {
        DocumentReference docRef = getMailboxCollection().document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        try {
            document = future.get(); // Wait for the operation to complete
        } catch (Exception e) {
            // 예외 처리 부분을 제거했으므로, 그냥 출력하고 종료하도록 할 수 있습니다.
            e.printStackTrace();
        }
        if (document != null && document.exists()) {
            return document.toObject(Mailbox.class);
        } else {
            // document가 null이거나 존재하지 않으면 null을 반환합니다.
            return null;
        }
    }

    public List<QueryDocumentSnapshot> findAllMailboxes() {
        ApiFuture<QuerySnapshot> future = getMailboxCollection().get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = future.get(); // Wait for the operation to complete
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 출력
        }
        if (querySnapshot != null) {
            return querySnapshot.getDocuments();
        } else {
            return null; // 데이터가 없으면 null 반환
        }
    }

    public void updateMailbox(Mailbox mailbox) {
        mailbox.setLetterCount(mailbox.getLetterIds().size());
        DocumentReference docRef = getMailboxCollection().document(mailbox.getId());
        ApiFuture<WriteResult> result = docRef.set(mailbox);
        try {
            result.get(); // Wait for the operation to complete
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 출력
        }
    }

    public void deleteMailboxById(String id) {
        DocumentReference docRef = getMailboxCollection().document(id);
        ApiFuture<WriteResult> result = docRef.delete();
        try {
            result.get(); // Wait for the operation to complete
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 출력
        }
    }

    // 사용자가 우편함 반경에 있는지 판단
    public boolean isWithinRadius(GeoPoint userLocation, Mailbox mailbox){
        double distance = calculateDistance(userLocation, mailbox.getLocation());
        return distance <= mailbox.getRadius();
    }

    private double calculateDistance(GeoPoint userLocation, GeoPoint mailboxLocation){
        // 위도, 경도 차이를 이용한 거리 계산
        double lat1 = userLocation.getLatitude();
        double lon1 = userLocation.getLongitude();
        double lat2 = mailboxLocation.getLatitude();
        double lon2 = mailboxLocation.getLongitude();

        // 거리 계산 로직 (예: Haversine formula)
        double earthRadius = 6371;  // km 단위
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;  // km 단위
    }

    public void addLetterToMailbox(String mailboxId, String letterId) {
        DocumentReference mailboxRef = getMailboxCollection().document(mailboxId);

        try {
            FirestoreClient.getFirestore().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(mailboxRef).get();
                if (!snapshot.exists()) {
                    throw new IllegalArgumentException("Mailbox not found");
                }

                Mailbox mailbox = snapshot.toObject(Mailbox.class);
                if (mailbox != null) {
                    mailbox.getLetterIds().add(letterId);
                    mailbox.setLetterCount(mailbox.getLetterIds().size());
                    transaction.set(mailboxRef, mailbox);
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removeLetterFromMailbox(String mailboxId, String letterId) {
        DocumentReference mailboxRef = getMailboxCollection().document(mailboxId);

        try {
            FirestoreClient.getFirestore().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(mailboxRef).get();
                if (!snapshot.exists()) {
                    throw new IllegalArgumentException("Mailbox not found");
                }

                Mailbox mailbox = snapshot.toObject(Mailbox.class);
                if (mailbox != null) {
                    mailbox.getLetterIds().remove(letterId);
                    mailbox.setLetterCount(mailbox.getLetterIds().size());
                    transaction.set(mailboxRef, mailbox);
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}