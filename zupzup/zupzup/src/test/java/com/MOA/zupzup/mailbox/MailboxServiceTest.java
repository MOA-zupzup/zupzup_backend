package com.MOA.zupzup.mailbox;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.MOA.zupzup.global.FirebaseConfig;
import com.google.cloud.firestore.GeoPoint;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-b25026bb30.json")
public class MailboxServiceTest {

    @Autowired
    private MailboxService mailboxService;

    @Autowired
    private FirebaseConfig firebaseConfig;

    @Autowired
    private Firestore firestore;

    private Mailbox mailbox;

    @BeforeEach
    void setUp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()){
        FileInputStream serviceAccount = new FileInputStream("C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-b25026bb30.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);
}
        mailbox = Mailbox.builder()
                .id("testMailbox123")
                .location(new GeoPoint(37,-122))
                .radius(100.0)
                .letterIds(List.of("letter1","letter2"))
                .build();
    }

    @Test
    void testSaveMailbox() throws ExecutionException, InterruptedException {
        mailboxService.saveMailbox(mailbox);

        DocumentReference docRef = firestore.collection("mailboxes").document(mailbox.getId());
        DocumentSnapshot document = docRef.get().get();

        assertTrue(document.exists());
        Mailbox savedMailbox = document.toObject(Mailbox.class);
        assertNotNull(savedMailbox);
        assertEquals(mailbox.getId(), savedMailbox.getId());
    }

    @Test
    void testGetMailbox() throws ExecutionException, InterruptedException{
        mailboxService.saveMailbox(mailbox);

        Mailbox retrievedMailbox = mailboxService.getMailboxById(mailbox.getId());

        assertNotNull(retrievedMailbox);
        assertEquals(mailbox.getId(),retrievedMailbox.getId());
    }
}