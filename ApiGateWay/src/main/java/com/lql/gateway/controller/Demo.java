package com.lql.gateway.controller;


import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/demo")
public class Demo {
    @GetMapping
    public Mono<Authentication> demo(Mono<Authentication> authentication) {
        return authentication.log().doOnNext(this::print);
    }
    public void print(Authentication authenticationMono) {
        System.out.println(authenticationMono);
    }
}
