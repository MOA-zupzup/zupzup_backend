package com.MOA.zupzup.member;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_NICKNAME = "testuser";

    @BeforeAll
    public static void setup() throws Exception {
        // Firebase initialization is already done in ZupzupApplication
    }

    @Test
    public void testCreateMember() throws Exception {
        // Check if the user already exists
        try {
            UserRecord existingUser = FirebaseAuth.getInstance().getUserByEmail(TEST_EMAIL);
            // If the user exists, delete the user
            FirebaseAuth.getInstance().deleteUser(existingUser.getUid());
        } catch (Exception e) {
            // User does not exist, proceed with creation
        }

        Member member = Member.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .nickname(TEST_NICKNAME)
                .build();

        UserRecord userRecord = memberService.createMember(member);
        assertNotNull(userRecord.getUid());
    }

    @Test
    public void testGetMember() throws Exception {
        // Ensure the user exists before trying to get it
        try {
            FirebaseAuth.getInstance().getUserByEmail(TEST_EMAIL);
        } catch (Exception e) {
            Member member = Member.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .nickname(TEST_NICKNAME)
                    .build();
            memberService.createMember(member);
        }

        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(TEST_EMAIL);
        assertNotNull(userRecord);
    }

    @Test
    public void testDeleteMember() throws Exception {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(TEST_EMAIL);
        memberService.deleteMember(userRecord.getUid());

        assertThrows(Exception.class, () -> {
            FirebaseAuth.getInstance().getUser(userRecord.getUid());
        });
    }
}