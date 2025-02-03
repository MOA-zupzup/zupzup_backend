package com.MOA.zupzup.mailbox;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private final Firestore firestore;

    public FirestoreService(Firestore firestore) {
        this.firestore = firestore;
    }

    public void addDocument(String collectionName, String documentId, Object data) {
        firestore.collection(collectionName).document(documentId).set(data);
    }

    public List<QueryDocumentSnapshot> getDocuments(String collectionName) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collection(collectionName).get();
        return query.get().getDocuments();
    }
}
