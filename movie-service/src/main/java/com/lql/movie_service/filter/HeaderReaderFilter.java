package com.lql.movie_service.filter;

import com.lql.movie_service.dto.CustomAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class HeaderReaderFilter extends AuthenticationWebFilter {
    public HeaderReaderFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
        setServerAuthenticationConverter(exchange -> {

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String userId = headers.getFirst("Id");
            String username = headers.getFirst("Name");
            String permissions = headers.getFirst("Permissions");
            List<SimpleGrantedAuthority> authorities = Arrays.stream(permissions.split(" "))
                    .parallel()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return Mono.just(new CustomAuthentication(userId, username, authorities));
        });
    }
}
