package com.MOA.zupzup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ZupzupApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ZupzupApplication.class, args);
    }
}