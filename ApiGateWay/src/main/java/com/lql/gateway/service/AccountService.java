package com.lql.gateway.service;


import com.lql.gateway.dto.UserAccount;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final Keycloak admin;
    private final KeycloakBuilder user;
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final WebClient webclient;

    public AccessTokenResponse grantToken(UserAccount account) {
        return user.username(account.username())
                .password(account.password())
                .build().tokenManager().getAccessToken();

    }
    public Mono<AccessTokenResponse> refreshToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        formData.add("client_id", "react-client");
        formData.add("client_secret", "65ZhWEkm6COgGImGtJwU1O31PQDTsQVn");

            return webclient
                    .post()
                    .uri("http://localhost:8000/realms/demo-realm/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(AccessTokenResponse.class);
    }


    public UserRepresentation createNewUser(UserAccount account) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(account.username());
        user.setEmail(account.email());
        user.setFirstName(account.firstname());
        user.setLastName(account.lastname());
        user.setEnabled(true);
        user.setGroups(List.of("user-group"));
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(account.password());

        user.setCredentials(List.of(credential));



        UsersResource users = admin.realm("demo-realm").users();
        Response response = users.create(user);
        String createdId = CreatedResponseUtil.getCreatedId(response);

        CompletableFuture<SendResult<Object, String>> create_account = kafkaTemplate.send("create_account", createdId);

        create_account
                .thenAccept(res ->
                        log.info("account {} created successfully at {}",
                                res.getProducerRecord().value(), res.getProducerRecord().timestamp()))
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });


        return admin.realm("demo-realm").users().get(createdId).toRepresentation();
    }
}
