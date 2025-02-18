package com.MOA.zupzup.mailbox;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class MailboxService {
    private final Firestore firestore;

    @Autowired
    public MailboxService(Firestore firestore){
        this.firestore = firestore;
    }

    public void saveMailbox(Mailbox mailbox) throws ExecutionException, InterruptedException{
        DocumentReference docRef = firestore.collection("mailboxes").document(mailbox.getId());
        ApiFuture<WriteResult> future = docRef.set(mailbox);
        future.get();
    }

    public Mailbox getMailboxById(String id) throws ExecutionException, InterruptedException{
        DocumentReference docRef = firestore.collection("mailboxes").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()){
            return document.toObject(Mailbox.class);
        } else {
            return null;
        }
    }
}