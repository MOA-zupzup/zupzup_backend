// src/main/java/com/MOA/zupzup/member/MemberService.java

package com.MOA.zupzup.member;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class MemberService {

    private static final String COLLECTION_MEMBER = "members";

    private CollectionReference getMemberCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_MEMBER);
    }

    // 사용자 정보 가져오기

    //-------------------편지지 관련-------------------//

    // 사용자가 소유한 편지지 목록 가져오기
    public List<Map<String, Object>> getAllOwnedStationery(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<Map<String, Object>> stationeryList = new ArrayList<>();

        CollectionReference ownedLetterRef = db.collection(COLLECTION_MEMBER)
                .document(userId)
                .collection("ownedLetter");

        for (DocumentSnapshot doc : ownedLetterRef.get().get().getDocuments()) {
            stationeryList.add(doc.getData());
        }

        return stationeryList;
    }

    // 사용자가 소유한 특정 편지지 가져오기
    public Map<String, Object> getOwnedStationery(String userId, String stationeryId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference stationeryRef = db.collection(COLLECTION_MEMBER)
                .document(userId)
                .collection("ownedLetter")
                .document(stationeryId);

        DocumentSnapshot document = stationeryRef.get().get();
        if (document.exists()) {
            Long count = document.getLong("count");
            if (count != null && count == 0) {
                throw new IllegalStateException("해당 편지지를 가지고 있지 않습니다");
            }
            return document.getData();
        }
        return null;
    }

    // 사용자가 소유한 특정 편지지의 imageUrl만 가져오기
    public String getOwnedStationeryImageUrl(String userId, String stationeryId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference stationeryRef = db.collection(COLLECTION_MEMBER)
                .document(userId)
                .collection("ownedLetter")
                .document(stationeryId);

        DocumentSnapshot document = stationeryRef.get().get();
        if (document.exists()) {
            Long count = document.getLong("count");
            if (count != null && count == 0) {
                throw new IllegalStateException("해당 편지지를 가지고 있지 않습니다");
            }
            return document.getString("imageUrl");
        }
        return null;
    }

    // 사용자가 소유한 편지지 삭제
    public void deleteOwnedStationery(String userId, String stationeryId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference stationeryRef = db.collection(COLLECTION_MEMBER)
                .document(userId)
                .collection("ownedLetter")
                .document(stationeryId);

        stationeryRef.delete().get();  // Firestore에서 삭제
    }

    // 특정 편지지 보유 수 반환
    public int setStationeryCount(String userId, String stationeryId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference stationeryRef = db.collection(COLLECTION_MEMBER)
                .document(userId)
                .collection("ownedLetter")
                .document(stationeryId);

        DocumentSnapshot document = stationeryRef.get().get();
        if (document.exists()) {
            Boolean isBasic = document.getBoolean("isBasic");
            if (isBasic != null && isBasic) {
                return -1;  // isBasic이 true이면 -1 반환
            } else {
                Long count = document.getLong("count");  // Firestore에서 int 값 가져오기
                return (count != null) ? count.intValue() : 0;  // null이면 기본값 0 반환
            }
        }
        return 0;  // 문서가 없으면 기본값 0 반환
    }

    // 특정 편지지 보유 수 업데이트
    public boolean updateStationeryCount(String userId, String stationeryId, int newCount) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference stationeryRef = db.collection(COLLECTION_MEMBER)
                .document(userId)
                .collection("ownedLetter")
                .document(stationeryId);

        DocumentSnapshot document = stationeryRef.get().get();
        if (document.exists()) {
            Boolean isBasic = document.getBoolean("isBasic");
            if (isBasic != null && isBasic) {
                return false;  // isBasic이 true이면 수량 수정 불가
            }

            if (newCount < 0) {
                return false;  // count가 음수일 경우 업데이트하지 않음
            }

            if (newCount == 0) {
                stationeryRef.delete().get();  // count가 0이면 문서 삭제
            } else {
                stationeryRef.update("count", newCount);
            }
            return true;  // 업데이트 성공
        }
        return false;  // 해당 문서가 없으면 업데이트 실패
    }
}