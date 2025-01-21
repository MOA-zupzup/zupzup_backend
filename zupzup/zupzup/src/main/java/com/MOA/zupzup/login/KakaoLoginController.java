package com.MOA.zupzup.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code/kakao")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping
    public ResponseEntity<?> callback(@RequestParam("code") String code) throws IOException {
        try {
            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            return new ResponseEntity<>(accessToken, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during Kakao login callback", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}