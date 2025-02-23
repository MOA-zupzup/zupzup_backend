package com.MOA.zupzup.letter;

import com.MOA.zupzup.global.exception.LetterException;
import com.MOA.zupzup.letter.dto.DroppingLetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.google.cloud.firestore.GeoPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutionException;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=")
public class LetterServiceTest {

    @Autowired
    private LetterService letterService;

    private String savedLetterId;

    private DroppingLetterRequest createLetterRequest(){
        DroppingLetterRequest request = new DroppingLetterRequest(
                "testTitle",
                "testContent",
                new GeoPoint(37,126),
                "testPictureUrl",
                "testPaperUrl",
                "testSenderId",
                "test0"
        );
        return request;
    }

    @Test
    void 편지_남기고_불러오기_성공() throws ExecutionException, InterruptedException {

        DroppingLetterRequest request = createLetterRequest();

        savedLetterId = letterService.createUnpickedLetter(request);

        System.out.println("savedId = " + savedLetterId);
        LetterResponse response = letterService.findLetter(savedLetterId);
        System.out.println("letter is created at: " + response.createdAt());
        assertNotNull(String.valueOf(response.id()), "편지 찾을 수 없음");
    }

    @Test
    void 편지_줍기_성공() {

        DroppingLetterRequest request = createLetterRequest();

        savedLetterId = letterService.createUnpickedLetter(request);

        System.out.println("savedId = " + savedLetterId);
        letterService.pickUpLetter(savedLetterId, "receiver");
        LetterResponse response = letterService.findLetter(savedLetterId);
        System.out.println("Receiver : " + response.receiverId());
        assertNotNull(String.valueOf(response.id()), "편지 찾을 수 없음");
    }

    @Test
    void 편지_줍기_에러_발생() {
        Assertions.assertThrows(LetterException.class, () -> {
            letterService.pickUpLetter("123", "who");
        });
    }
}

