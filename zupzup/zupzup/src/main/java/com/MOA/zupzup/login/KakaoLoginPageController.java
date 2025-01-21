package com.MOA.zupzup.login;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class KakaoLoginPageController {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @GetMapping
    public String loginPage(Model model) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("redirectUri", redirectUri);
        return "login"; // Ensure that there is a login.html in src/main/resources/templates
    }
}