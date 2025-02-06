package com.MOA.zupzup.letter;

import com.MOA.zupzup.letter.dto.LetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.MOA.zupzup.login.FirebaseConfig;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutionException;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=src/main/resources/zupzup-e3e05-firebase-adminsdk-1ujhj-725b926874.json")
public class LetterServiceTest {

    @Autowired
    private LetterService letterService;
    private FirebaseConfig firebaseConfig;

    private LetterRequest createLetterRequest(){
        LetterRequest request = new LetterRequest(
                "testId",
                "testTitle",
                "testContent",
                new GeoPoint(37,126),
                Timestamp.now(),
                "testPictureUrl",
                "testPaperUrl",
                "testSenderId"
        );
        return request;
    }

    @Test
    void testSaveAndRetrieveLetter() throws ExecutionException, InterruptedException {

        LetterRequest request = createLetterRequest();

         String savedId = letterService.createUnpickedLetter(request);

        System.out.println("savedId = " + savedId);
        LetterResponse response = letterService.findLetter(savedId);
        System.out.println("letter is created at: " + response.createdAt());
        assertNotNull(String.valueOf(response.id()), "편지 찾을 수 없음");
    }
}

