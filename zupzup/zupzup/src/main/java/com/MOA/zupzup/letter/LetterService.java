package com.MOA.zupzup.letter;

import com.MOA.zupzup.common.FirebaseService;
import com.MOA.zupzup.letter.dto.LetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class LetterService {

    private static final String COLLECTION_NAME = "letters";

    private CollectionReference getLetterCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_NAME);
    }

    public String createUnpickedLetter(LetterRequest request){
        Letter letter = Letter.dropLetter(request);
        createLetter(letter);
        return letter.getId();
    }

    public LetterResponse findLetter(String id){
        Letter letter = findLetterById(id);
        return LetterResponse.from(letter);
    }

    public String createLetter(Letter letter) {

        DocumentReference docRef = getLetterCollection().document();
        letter.setId(docRef.getId());
        ApiFuture<WriteResult> letterApiFuture = docRef.set(letter);

        try {
            letterApiFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return docRef.getId();
    }

    public Letter findLetterById (String id) {
        DocumentReference docRef = getLetterCollection().document();
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;

        try {
            document = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(document.exists()) {
            return document.toObject(Letter.class);
        }

        return null;
    }

    public List<QueryDocumentSnapshot> findAllLetters() {
        ApiFuture<QuerySnapshot> future = getLetterCollection().get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return querySnapshot.getDocuments();
    }

    public void updateLetter(String id, Letter letter) {
        DocumentReference docRef = getLetterCollection().document(id);
        ApiFuture<com.google.cloud.firestore.WriteResult> future = docRef.set(letter);
        try {
            future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLetter(String id) {
        DocumentReference docRef = getLetterCollection().document(id);
        ApiFuture<com.google.cloud.firestore.WriteResult> writeResult = docRef.delete();
        try {
            writeResult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

/*
    private final FirebaseService<Letter> firebaseService;

    public void createUnpickedLetter(LetterRequest request){
        Letter letter = Letter.dropLetter(request);
        firebaseService.create(COLLECTION_NAME, letter.getId(), letter);
    }

    public LetterResponse findLetter(String id){
        Letter letter = firebaseService.findById(COLLECTION_NAME, id, Letter.class);
        return LetterResponse.from(letter);
    }
*/
}
