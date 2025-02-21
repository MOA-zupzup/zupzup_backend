package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.global.exception.ErrorCode;
import com.MOA.zupzup.global.exception.MailboxException;
import com.MOA.zupzup.global.exception.MailboxNotFoundException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailboxService {

    private static final String COLLECTION_NAME = "mailboxes";

    private CollectionReference getMailboxCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_NAME);
    }


    public Mailbox createMailbox(Mailbox mailbox, String mailboxId) {
        DocumentReference docRef = getMailboxCollection().document(mailboxId);

        try {
            FirestoreClient.getFirestore().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(docRef).get();
                if (snapshot.exists()) {
                    throw new MailboxException(ErrorCode.MAILBOX_ALREADY_EXISTS);
                }
                mailbox.setId(mailboxId);
                transaction.set(docRef, mailbox);
                return null;
            }).get();
            return mailbox;
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_CREATE_FAILED);
        }
    }

    public Mailbox findMailboxById(String id) {
        try {
            DocumentReference docRef = getMailboxCollection().document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return handleFirestoreResult(document, () -> new MailboxException(ErrorCode.MAILBOX_NOT_FOUND));
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_FIND_FAILED);
        }
    }

    public Mailbox getMailboxById(String id) {
        Mailbox mailbox = findMailboxById(id);  // Firestore에서 우편함 조회
        if (mailbox == null) {
            throw new MailboxNotFoundException("Mailbox with id " + id + " not found");
        }
        // 추가적인 검증 로직을 여기서 처리
        return mailbox;
    }

    public List<QueryDocumentSnapshot> findAllMailboxes() {
        try {
            ApiFuture<QuerySnapshot> future = getMailboxCollection().get();
            QuerySnapshot querySnapshot = future.get();
            if (querySnapshot == null || querySnapshot.isEmpty()) {
                throw new MailboxException(ErrorCode.NO_MAILBOXES_FOUND);
            }
            return querySnapshot.getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_FIND_FAILED);
        }
    }

    public void updateMailbox(Mailbox mailbox) {
        DocumentReference docRef = getMailboxCollection().document(mailbox.getId());

        try {
            FirestoreClient.getFirestore().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(docRef).get();
                if (!snapshot.exists()) {
                    throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND);
                }

                mailbox.setLetterCount(mailbox.getLetterIds().size());
                transaction.set(docRef, mailbox);
                return null;  // 트랜잭션 완료
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteMailboxById(String id) {
        DocumentReference docRef = getMailboxCollection().document(id);
        try {
            DocumentSnapshot snapshot = docRef.get().get();
            if (!snapshot.exists()) {
                throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND); // 존재하지 않으면 예외 발생
            }
            docRef.delete().get(); // 삭제 실행
        } catch (InterruptedException | ExecutionException e) {
            throw new MailboxException(ErrorCode.MAILBOX_DELETE_FAILED);
        }
    }

    // 사용자가 우편함 반경에 있는지 판단
    public boolean isWithinRadius(GeoPoint userLocation, Mailbox mailbox) {
        // 예외 처리: null 값 체크 (MailboxException 사용)
        if (userLocation == null) {
            throw new MailboxException(ErrorCode.INVALID_USER_LOCATION);  // ErrorCode에 맞는 값 사용
        }
        if (mailbox == null || mailbox.getLocation() == null) {
            throw new MailboxException(ErrorCode.INVALID_MAILBOX_LOCATION);  // ErrorCode에 맞는 값 사용
        }

        // 위도, 경도 차이를 비교하여 너무 멀리 떨어져 있으면 빠르게 필터링
        double lat1 = userLocation.getLatitude();
        double lon1 = userLocation.getLongitude();
        double lat2 = mailbox.getLocation().getLatitude();
        double lon2 = mailbox.getLocation().getLongitude();

        // 위도, 경도 차이가 너무 크면 바로 false 반환
        if (Math.abs(lat1 - lat2) > 0.1 || Math.abs(lon1 - lon2) > 0.1) {
            return false; // 너무 차이가 나면 반경 내에 있을 수 없음
        }

        // Haversine 공식으로 실제 거리 계산
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

    @Transactional
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
                    mailbox.addLetter(letterId);
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

    @Transactional
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
                    mailbox.removeLetter(letterId);
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