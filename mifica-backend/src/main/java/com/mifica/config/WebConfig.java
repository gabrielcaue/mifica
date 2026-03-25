package com.mifica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ICP-TOTAL: 1
    // ICP-01: Configuração define integração web global (CORS/interceptação) para toda API.
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:8080",
                "https://gabrielcaue.github.io"
            )
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
