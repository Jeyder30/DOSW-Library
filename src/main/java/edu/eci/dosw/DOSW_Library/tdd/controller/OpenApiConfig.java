package edu.eci.dosw.DOSW_Library.tdd.controller;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DOSW Library API")
                        .version("1.0.0")
                        .description("API para la gestion de libros, usuarios y prestamos de la biblioteca")
                        .contact(new Contact().name("Equipo DOSW"))
                        .license(new License().name("Academic Use")));
    }
}
