package com.example.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {                                    
    @Bean
    public OpenAPI apiInfo() { 
        return new OpenAPI()
            .info(new Info().title("API Microservice")
            .description("API 微服務文件")
            .version("v1.0"))
            .externalDocs(new ExternalDocumentation()
            .description("專案首頁")
            .url("http://localhost:8080"));
    }
}
