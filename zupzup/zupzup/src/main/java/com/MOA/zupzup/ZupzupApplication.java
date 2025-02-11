package com.MOA.zupzup;

import com.MOA.zupzup.login.FirebaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ZupzupApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ZupzupApplication.class, args);
    }
}