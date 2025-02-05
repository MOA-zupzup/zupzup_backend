package com.MOA.zupzup.letter;

import com.MOA.zupzup.letter.dto.LetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
public class LetterServiceTest {

    @Autowired
    private LetterService letterService;

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

        LetterResponse response = letterService.findLetter(savedId);
        assertNotNull(String.valueOf(response.id()), "편지 찾을 수 없음");
    }
}

