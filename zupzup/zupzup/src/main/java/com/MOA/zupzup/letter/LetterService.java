package com.MOA.zupzup.letter;

import com.MOA.zupzup.global.exception.ErrorCode;
import com.MOA.zupzup.global.exception.LetterException;
import com.MOA.zupzup.letter.dto.DroppingLetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LetterService {

    private static final String COLLECTION_NAME = "letters";

    private CollectionReference getLetterCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_NAME);
    }

    @Transactional
    public String createUnpickedLetter(DroppingLetterRequest request){
        Letter letter = request.toDropLetterEntity();
        return createLetter(letter);
    }

    public LetterResponse findLetter(String letterId)  {
        Letter letter = findLetterById(letterId);
        return LetterResponse.from(letter);
    }

    public List<LetterResponse> findAllLettersByMemberId(String memberId) {
        List<Letter> letters = findLettersByMemberId(memberId);
        return letters.stream().map(LetterResponse::from).toList();
    }

    @Transactional
    public void pickUpLetter(String letterId, String receiverId) {
        Letter letter = findLetterById(letterId);
        letter.pickUp(receiverId);
        updateLetter(letter);
    }

    @Transactional
    public void deleteLetter(String letterId) {
        findLetterById(letterId);
        deleteLetterById(letterId);
    }


    //=== firestore CRUD 메서드 ===//
    private String createLetter(Letter letter) {
        try {
            DocumentReference docRef = getLetterCollection().document();
            letter.setId(docRef.getId());
            ApiFuture<WriteResult> letterApiFuture = docRef.set(letter);
            letterApiFuture.get();
            return docRef.getId();
        } catch (InterruptedException | ExecutionException e) {
            throw new LetterException(ErrorCode.LETTER_CREATE_FAILED);
        }
    }

    private Letter findLetterById(String id) {
        try {
            DocumentReference docRef = getLetterCollection().document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return handleFirestoreResult(document, () -> new LetterException(ErrorCode.LETTTER_NOT_FOUND));
        } catch (InterruptedException | ExecutionException e) {
            throw new LetterException(ErrorCode.LETTER_FIND_FAILED);
        }
    }

    private List<QueryDocumentSnapshot> findAllLetters() {
        try {
            ApiFuture<QuerySnapshot> future = getLetterCollection().get();
            QuerySnapshot querySnapshot = future.get();
            return querySnapshot.getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            throw new LetterException(ErrorCode.LETTER_FIND_FAILED);
        }
    }

    public List<Letter> findLettersByMemberId(String memberId) {
        try {
            ApiFuture<QuerySnapshot> queryReceiver = getLetterCollection()
                    .whereEqualTo("receiverId", memberId)
                    .get();
            ApiFuture<QuerySnapshot> querySender = getLetterCollection()
                    .whereEqualTo("senderId", memberId)
                    .get();

            List<QueryDocumentSnapshot> receiverDocs = queryReceiver.get().getDocuments();
            List<QueryDocumentSnapshot> senderDocs = querySender.get().getDocuments();

            Set<Letter> letterSet = Stream.concat(receiverDocs.stream(), senderDocs.stream())
                    .map(doc -> doc.toObject(Letter.class))
                    .filter(letter -> letter != null)
                    .collect(Collectors.toSet());

            return new ArrayList<>(letterSet);
        } catch (InterruptedException | ExecutionException e) {
            throw new LetterException(ErrorCode.FAIL_TO_FIND_MY_LETTERS);
        }
    }

    private void updateLetter(Letter letter) {
        try {
            String id = letter.getId();
            DocumentReference docRef = getLetterCollection().document(letter.getId());
            ApiFuture<WriteResult> result = docRef.set(letter);
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new LetterException(ErrorCode.LETTER_UPDATE_FAILED);
        }
    }

    private void deleteLetterById(String id) {
        try {
            DocumentReference docRef = getLetterCollection().document(id);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new LetterException(ErrorCode.LETTER_DELETE_FAILED);
        }
    }

    //=== 검증 메서드===//
    private Letter handleFirestoreResult(DocumentSnapshot documentSnapshot, Supplier<? extends RuntimeException> supplier){
        return documentSnapshot.exists() ? documentSnapshot.toObject(Letter.class) : throwException(supplier);
    }

    private <T> T throwException(Supplier<? extends RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.get();
    }
}
