package com.MOA.zupzup.mailbox;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootTest
public class FirestoreServiceTest {

    @Autowired
    private MailboxService mailboxService;

    @BeforeAll
    public static void setup() throws Exception {
        System.setProperty("firebase.service-account.path","C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-4cd17a4987.json");
    }

    @Test
    public void testAddLetterToMailbox() throws ExecutionException, InterruptedException {
        Long mailboxId = 1L; // 테스트할 Mailbox ID
        Long letterId = 101L; // 추가할 편지 ID

        // 편지를 Mailbox에 추가
        mailboxService.addLetterToMailbox(mailboxId, letterId);

        // Firestore에서 Mailbox 조회
        List<Mailbox> mailboxes = mailboxService.getMailboxes();

        // 콘솔로 결과 확인
        mailboxes.forEach(System.out::println);
    }

    @Test
    public void testGetMailboxes() throws ExecutionException, InterruptedException {
        // Firestore에서 Mailboxes 조회
        List<Mailbox> mailboxes = mailboxService.getMailboxes();

        // 조회한 Mailboxes 출력
        mailboxes.forEach(System.out::println);
    }
}
