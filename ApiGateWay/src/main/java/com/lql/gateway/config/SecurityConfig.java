package com.lql.gateway.config;

import com.lql.gateway.dto.CustomAuthentication;
import com.lql.gateway.filter.ShrinkAuthFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> {
                    corsSpec.configurationSource(request -> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
                        configuration.setAllowedMethods(List.of("*"));
                        configuration.setAllowedHeaders(List.of("*"));
                        return configuration;
                    });
                })
                .authorizeExchange(exchange -> exchange
                                                    .pathMatchers("/auth/**").permitAll()
                                                    .anyExchange().authenticated())
                .addFilterAfter(new ShrinkAuthFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
                .build();

    }
    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    String permissions = auth.getAuthorities().parallelStream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(" "));
                    ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
                    mutate.header("Id", ((CustomAuthentication) auth).getId());
                    mutate.header("Name", auth.getName());
                    mutate.header("Permissions",  permissions);

                    return chain.filter(exchange);
                });
    }

}
