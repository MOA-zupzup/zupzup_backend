package com.MOA.zupzup.mailbox;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    public void addDocument(String collectionName, String documentId, Map<String, Object> data) {
        firestore.collection(collectionName).document(documentId).set(data);
    }

    public List<QueryDocumentSnapshot> getDocuments(String collectionName) {
        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
        try {
            return future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Map<String, Object> convertMailboxToFirestoreData(Mailbox mailbox) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", mailbox.getId());
        data.put("location", new GeoPoint(mailbox.getCenterLatitude(), mailbox.getCenterLongitude()));
        data.put("radius", mailbox.getRadius());
        data.put("letterIds", mailbox.getLetterIds());
        return data;
    }
}
