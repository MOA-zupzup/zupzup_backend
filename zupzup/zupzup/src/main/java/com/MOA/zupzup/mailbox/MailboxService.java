package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.global.exception.ErrorCode;
import com.MOA.zupzup.global.exception.MailboxException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class MailboxService {

    private static final String COLLECTION_NAME = "mailboxes";

    private CollectionReference getMailboxCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_NAME);
    }

    public Mailbox createMailbox(Mailbox mailbox, String mailboxId) {
        try {
            DocumentReference docRef = getMailboxCollection().document(mailboxId);
            mailbox.setId(docRef.getId());
            ApiFuture<WriteResult> mailboxApiFuture = docRef.set(mailbox);
            mailboxApiFuture.get();
            return mailbox;
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_CREATE_FAILED); // 예외 던지기
        }
    }

    public Mailbox findMailboxById(String id) {
        try {
            DocumentReference docRef = getMailboxCollection().document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return handleFirestoreResult(document, () -> new MailboxException(ErrorCode.MAILBOX_NOT_FOUND)); // 예외 처리
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_FIND_FAILED);
        }
    }

    public List<QueryDocumentSnapshot> findAllMailboxes() {
        ApiFuture<QuerySnapshot> future = getMailboxCollection().get();
        try {
            QuerySnapshot querySnapshot = future.get();
            return querySnapshot != null ? querySnapshot.getDocuments() : Collections.emptyList();  // 빈 리스트 반환
        } catch (Exception e) {
            return Collections.emptyList();  // 예외 발생 시 빈 리스트 반환
        }
    }

    public void updateMailbox(Mailbox mailbox) {
        mailbox.setLetterCount(mailbox.getLetterIds().size());
        DocumentReference docRef = getMailboxCollection().document(mailbox.getId());
        ApiFuture<WriteResult> result = docRef.set(mailbox);
        try {
            result.get(); // Wait for the operation to complete
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_UPDATE_FAILED); // 던지기
        }
    }

    public void deleteMailboxById(String id) {
        DocumentReference docRef = getMailboxCollection().document(id);
        ApiFuture<WriteResult> result = docRef.delete();
        try {
            result.get(); // Wait for the operation to complete
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_DELETE_FAILED);  // 예외 던지기
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
                    throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND);
                }

                Mailbox mailbox = snapshot.toObject(Mailbox.class);
                if (mailbox != null) {
                    mailbox.getLetterIds().add(letterId);
                    mailbox.setLetterCount(mailbox.getLetterIds().size());
                    transaction.set(mailboxRef, mailbox);
                } else {
                    throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND);
                }
                return null;  // 트랜잭션 완료
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_UPDATE_FAILED); // 예외 처리
        }
    }

    //=== 편지 작성 시 우편함에 추가 ===//


    public void removeLetterFromMailbox(String mailboxId, String letterId) {
        DocumentReference mailboxRef = getMailboxCollection().document(mailboxId);

        try {
            FirestoreClient.getFirestore().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(mailboxRef).get();
                if (!snapshot.exists()) {
                    throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND);
                }

                Mailbox mailbox = snapshot.toObject(Mailbox.class);
                if (mailbox != null) {
                    mailbox.getLetterIds().remove(letterId);
                    mailbox.setLetterCount(mailbox.getLetterIds().size());
                    transaction.set(mailboxRef, mailbox);
                } else {
                    throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND);
                }
                return null;  // 트랜잭션 완료
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_UPDATE_FAILED); // 예외 처리
        }
    }

    //=== 검증 메서드===//
    private Mailbox handleFirestoreResult(DocumentSnapshot documentSnapshot, Supplier<? extends RuntimeException> supplier){
        return documentSnapshot.exists() ? documentSnapshot.toObject(Mailbox.class) : throwException(supplier);
    }

    private <T> T throwException(Supplier<? extends RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.get();
    }

}