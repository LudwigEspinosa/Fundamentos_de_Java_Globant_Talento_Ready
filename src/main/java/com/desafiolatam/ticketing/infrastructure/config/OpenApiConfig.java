package com.desafiolatam.ticketing.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenAPI 3 / Swagger-UI configuration bean.
 * Strictly isolated to development and test profiles (@Profile({"dev", "test"})).
 */
@Configuration
@Profile({"dev", "test"})
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NeonPulse Ticketing Platform - API Contract")
                        .version("1.0.0")
                        .description("Microservicio backend para gestión, cartelera y emisión de entradas para eventos desarrollado con Spring Boot 3 y Clean Architecture.")
                        .contact(new Contact()
                                .name("Equipo NeonPulse - Globant Talento Ready")
                                .email("soporte@neonpulse.latam.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
