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
        return document.exists() ? document.getData() : null;
    }

//    // 사용자가 소유한 편지지 추가
//    public void addOwnedStationery(String userId, String stationeryId, Map<String, Object> stationeryData) throws ExecutionException, InterruptedException {
//        Firestore db = FirestoreClient.getFirestore();
//
//        // 필수 필드가 있는지 확인하고 기본값 설정
//        String imageUrl = (String) stationeryData.getOrDefault("imageUrl", "");
//        String name = (String) stationeryData.getOrDefault("name", "");
//        Boolean isBasic = (Boolean) stationeryData.getOrDefault("isBasic", false);
//        int count = isBasic ? -1 : ((stationeryData.get("count") instanceof Number) ? ((Number) stationeryData.get("count")).intValue() : 0);
//
//        // Firestore에 저장할 데이터 생성
//        Map<String, Object> data = Map.of(
//                "imageUrl", imageUrl,
//                "name", name,
//                "isBasic", isBasic,
//                "count", count
//        );
//
//        DocumentReference stationeryRef = db.collection(COLLECTION_MEMBER)
//                .document(userId)
//                .collection("ownedLetter")
//                .document(stationeryId);
//
//        stationeryRef.set(data).get();  // Firestore에 저장
//    }

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

            stationeryRef.update("count", newCount);
            return true;  // 업데이트 성공
        }
        return false;  // 해당 문서가 없으면 업데이트 실패
    }
}