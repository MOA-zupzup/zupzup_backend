package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.GeoPoint;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-b25026bb30.json")
public class MailboxServiceTest {

    private MailboxService mailboxService;

    @BeforeEach
    void setUp() {
        mailboxService = new MailboxService();
    }

    @Test
    void testCreateMailbox() {
        Mailbox mailbox = new Mailbox();
        String Id = "test0";
        mailbox.setLocation(new GeoPoint(37.5665, 126.9780)); // 서울 위도, 경도
        mailbox.setRadius(10.0);
        mailbox.setLetterIds(new ArrayList<>()); // 빈 리스트

        String mailboxId = mailboxService.createMailbox(mailbox, Id);

        assertNotNull(mailboxId, "Mailbox ID should not be null");
        Mailbox createdMailbox = mailboxService.findMailboxById(mailboxId);
        assertNotNull(createdMailbox, "Created mailbox should be found");
        assertEquals(0, createdMailbox.getLetterCount(), "Initial letter count should be 0");
    }

    @Test
    void testAddLetterToMailbox() {
        Mailbox mailbox = new Mailbox();
        mailbox.setLocation(new GeoPoint(37.5665, 126.9780));
        mailbox.setRadius(10.0);
        mailbox.setLetterIds(new ArrayList<>());

        String mailboxId = mailboxService.createMailbox(mailbox,"addLetterTest");
        mailboxService.addLetterToMailbox(mailboxId, "letter1");

        Mailbox updatedMailbox = mailboxService.findMailboxById(mailboxId);
        assertNotNull(updatedMailbox);
        assertEquals(1, updatedMailbox.getLetterCount(), "Letter count should be 1 after adding a letter");
    }

    @Test
    void testRemoveLetterFromMailbox() {
        Mailbox mailbox = new Mailbox();
        mailbox.setLocation(new GeoPoint(37.5665, 126.9780));
        mailbox.setRadius(10.0);
        List<String> letterIds = new ArrayList<>();
        letterIds.add("letter1");
        mailbox.setLetterIds(letterIds);

        String mailboxId = mailboxService.createMailbox(mailbox,"removeLetterTest");
        mailboxService.removeLetterFromMailbox(mailboxId, "letter1");

        Mailbox updatedMailbox = mailboxService.findMailboxById(mailboxId);
        assertNotNull(updatedMailbox);
        assertEquals(0, updatedMailbox.getLetterCount(), "Letter count should be 0 after removing the letter");
    }
}
