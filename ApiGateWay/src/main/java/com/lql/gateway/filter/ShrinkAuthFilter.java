package com.lql.gateway.filter;

import com.lql.gateway.dto.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;

public class ShrinkAuthFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).contextWrite(this::customizeAuthentication);
    }


    public CustomAuthentication toCustomAuthentication(Authentication authentication) {
        Jwt jwt = ((Jwt) authentication.getPrincipal());
            String id = authentication.getName();
            List<? extends GrantedAuthority> roles = getRoles(jwt);
            String name = jwt.getClaimAsString("name");
            return new CustomAuthentication(
                    id,
                    name == null ? jwt.getClaimAsString("preferred_username") : name,
                    roles);
    }

    public List<? extends GrantedAuthority> getRoles(Jwt jwt) {

        Map<String, Object> resource_access = jwt.getClaimAsMap("resource_access");
        List<String> roles = (List) ((Map) resource_access.get("react-client")).get("roles");

        return roles.parallelStream().map(SimpleGrantedAuthority::new).toList();
    }


    private Context customizeAuthentication(Context context) {
        Mono<SecurityContextImpl> security = context.getOrDefault(SecurityContext.class, null);
        Mono<SecurityContextImpl> contextImpl = security.log()
                .flatMap(a -> Mono.just(a.getAuthentication()))
                .map(this::toCustomAuthentication).map(SecurityContextImpl::new);

        DefaultServerWebExchange  old = context.getOrDefault(ServerWebExchange.class, null);

        Context context1 = Context.of(SecurityContext.class, contextImpl);
        context1.put(ServerWebExchange.class, old);
        return context1;
    }
}
