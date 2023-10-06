package com.lql.gateway.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8000")
                .realm("demo-realm") // Replace with your Keycloak realm name
                .clientId("demo-client")
                .clientSecret("O4IzIDaqpwrZUuCX3fu7gzHRbLCVDyHn")
                .grantType("client_credentials")
                .build();
    }
    @Bean
    public KeycloakBuilder keycloak2 () {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8000")
                .realm("demo-realm")
                .clientId("react-client")
                .clientSecret("65ZhWEkm6COgGImGtJwU1O31PQDTsQVn")
                .grantType("password");
    };
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public NewTopic newTopic() {
        return new NewTopic("create_account", 1, (short) 1);
    }
}
