package com.MOA.zupzup.common;

import com.MOA.zupzup.letter.Letter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirebaseService<T> {

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    private CollectionReference getCollection(String collectionName) {
        return getFirestore().collection(collectionName);
    }

    public String create(String collectionName, String id, T data) {
        DocumentReference docRef = getCollection(collectionName).document(id);
        ApiFuture<WriteResult> result = docRef.set(data);
        try {
            result.get(); // 실행 완료 대기
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public T findById(String collectionName, String id, Class<T> clazz) {
        DocumentReference docRef = getCollection(collectionName).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return document.exists() ? document.toObject(clazz) : null;
    }

    public List<T> findAll(String collectionName, Class<T> clazz) {
        ApiFuture<QuerySnapshot> future = getCollection(collectionName).get();
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return documents.stream().map(doc -> doc.toObject(clazz)).collect(Collectors.toList());
    }

    public String update(String collectionName, String id, T data) {
        DocumentReference docRef = getCollection(collectionName).document(id);
        ApiFuture<WriteResult> result = docRef.set(data);
        try {
            result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public String delete(String collectionName, String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCollection(collectionName).document(id);
        ApiFuture<WriteResult> result = docRef.delete();
        result.get();
        return id;
    }
}
