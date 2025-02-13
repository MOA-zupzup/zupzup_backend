package com.MOA.zupzup.letter;

import com.MOA.zupzup.global.exception.LetterException;
import com.MOA.zupzup.letter.dto.DroppingLetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.MOA.zupzup.global.FirebaseConfig;
import com.google.cloud.firestore.GeoPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutionException;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = "firebase.service-account.path=C:/Users/jiin0/OneDrive/MoaGit/zupzup/zupzup-e3e05-firebase-adminsdk-1ujhj-4cd17a4987.json")
public class LetterServiceTest {

    @Autowired
    private LetterService letterService;
    private FirebaseConfig firebaseConfig;

    private DroppingLetterRequest createLetterRequest(){
        DroppingLetterRequest request = new DroppingLetterRequest(
                "testTitle",
                "testContent",
                new GeoPoint(37,126),
                "testPictureUrl",
                "testPaperUrl",
                "testSenderId"
        );
        return request;
    }

    @Test
    void 편지_남기고_불러오기_성공() throws ExecutionException, InterruptedException {

        DroppingLetterRequest request = createLetterRequest();

        String savedId = letterService.createUnpickedLetter(request);

        System.out.println("savedId = " + savedId);
        LetterResponse response = letterService.findLetter(savedId);
        System.out.println("letter is created at: " + response.createdAt());
        assertNotNull(String.valueOf(response.id()), "편지 찾을 수 없음");
    }

    @Test
    void 편지_줍기_성공() {

        DroppingLetterRequest request = createLetterRequest();

        String savedId = letterService.createUnpickedLetter(request);

        System.out.println("savedId = " + savedId);
        letterService.pickUpLetter(savedId, "receiver");
        LetterResponse response = letterService.findLetter(savedId);
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

