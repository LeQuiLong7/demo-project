package com.lql.gateway.controller;


import com.lql.gateway.dto.UserAccount;
import com.lql.gateway.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    private final AccountService accountService;


    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
    @PostMapping("/login")
    public Mono<AccessTokenResponse> login(@RequestBody Mono<UserAccount> userAccountMono) {
        return userAccountMono.log().map(accountService::grantToken);
    }

    @PostMapping("/register")
    public Mono<UserRepresentation> createUser(@RequestBody Mono<UserAccount> userAccountMono) {

        return userAccountMono.map(accountService::createNewUser);
    }
    @PostMapping("/refresh")
    public Mono<AccessTokenResponse> refresh(@RequestPart String refreshToken) {
        return accountService.refreshToken(refreshToken);
    }


}