package com.MOA.zupzup.mailbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-b25026bb30.json")
public class MailboxServiceTest {

    @Autowired
    private MailboxService mailboxService;

    private static final String TEST_MAILBOX_ID = "testMailbox";
    private static final String TEST_LETTER_ID = "testLetter";

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        // Firestore에 테스트용 Mailbox 데이터 추가
        Mailbox mailbox = new Mailbox();
        mailbox.setId(TEST_MAILBOX_ID);
        mailbox.setRadius(100);
        mailbox.setLetterIds(Arrays.asList("letter1", "letter2"));

        mailboxService.createMailbox(mailbox, TEST_MAILBOX_ID);
    }

    @AfterEach
    void tearDown() throws ExecutionException, InterruptedException {
        // Firestore에서 테스트 데이터 삭제
        mailboxService.deleteMailboxById(TEST_MAILBOX_ID);
    }

    @Test
    void testFindMailboxById() throws ExecutionException, InterruptedException {
        Mailbox mailbox = mailboxService.findMailboxById(TEST_MAILBOX_ID);

        assertNotNull(mailbox);
        assertEquals(TEST_MAILBOX_ID, mailbox.getId());
        assertEquals(100, mailbox.getRadius());
        assertEquals(2, mailbox.getLetterIds().size());
    }

    @Test
    void testUpdateMailbox() throws ExecutionException, InterruptedException {
        Mailbox mailbox = mailboxService.findMailboxById(TEST_MAILBOX_ID);
        mailbox.setRadius(200); // 반경 변경

        mailboxService.updateMailbox(mailbox);
        Mailbox updatedMailbox = mailboxService.findMailboxById(TEST_MAILBOX_ID);

        assertEquals(200, updatedMailbox.getRadius()); // 변경 확인
    }

    @Test
    void testAddLetterToMailbox() throws ExecutionException, InterruptedException {
        mailboxService.addLetterToMailbox(TEST_MAILBOX_ID, TEST_LETTER_ID);
        Mailbox mailbox = mailboxService.findMailboxById(TEST_MAILBOX_ID);

        assertTrue(mailbox.getLetterIds().contains(TEST_LETTER_ID));
    }

    @Test
    void testRemoveLetterFromMailbox() throws ExecutionException, InterruptedException {
        mailboxService.addLetterToMailbox(TEST_MAILBOX_ID, TEST_LETTER_ID);
        mailboxService.removeLetterFromMailbox(TEST_MAILBOX_ID, TEST_LETTER_ID);
        Mailbox mailbox = mailboxService.findMailboxById(TEST_MAILBOX_ID);

        assertFalse(mailbox.getLetterIds().contains(TEST_LETTER_ID));
    }
}
