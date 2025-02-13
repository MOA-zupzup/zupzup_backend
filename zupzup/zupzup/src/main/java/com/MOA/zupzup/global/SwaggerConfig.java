package com.MOA.zupzup.global;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .openapi("3.0.1")  // OpenAPI 버전 명시
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("줍줍")
                .version("1.0.0");
    }
}
