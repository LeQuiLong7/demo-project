package com.lql.movie_service.filter;

import com.lql.movie_service.dto.CustomAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CustomSecurityContextRepository implements ServerSecurityContextRepository {


    private final Map<String, SecurityContext> contextMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userId = headers.getFirst("Id");
        String username = headers.getFirst("Name");
        String permissions = headers.getFirst("Permissions");
        List<SimpleGrantedAuthority> authorities = Arrays.stream(permissions.split(" "))
                .parallel()
                .map(SimpleGrantedAuthority::new)
                .toList();

        context.setAuthentication(new CustomAuthentication(userId, username, authorities));
        contextMap.put(userId, context);
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst("Id");;
        if (userId == null || !contextMap.containsKey(userId)) {
            return Mono.empty();
        }
        return Mono.just(contextMap.get(userId));
    }
}
