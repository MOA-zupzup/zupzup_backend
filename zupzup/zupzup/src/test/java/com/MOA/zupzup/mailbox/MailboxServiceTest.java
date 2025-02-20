package com.MOA.zupzup.mailbox;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.WriteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-b25026bb30.json")
public class MailboxServiceTest {

    @InjectMocks
    private MailboxService mailboxService;

    @Mock
    private DocumentReference docRef;

    @Mock
    private ApiFuture<WriteResult> apiFuture; // ApiFuture로 모킹

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMailbox() throws ExecutionException, InterruptedException {
        // Arrange
        GeoPoint location = new GeoPoint(37, -122);
        List<String> letterIds = Arrays.asList("letter1","letter2","letter3");
        Mailbox mailbox = new Mailbox("1", location, 10.0, letterIds, 3);

        // ApiFuture를 모킹하여 WriteResult를 반환
        WriteResult writeResult = mock(WriteResult.class); // WriteResult 모킹
        when(docRef.getId()).thenReturn("1");
        when(docRef.set(mailbox)).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(writeResult); // ApiFuture.get() 호출 시, WriteResult 반환

        // Act
        String result = mailboxService.createMailbox(mailbox);

    }
}
