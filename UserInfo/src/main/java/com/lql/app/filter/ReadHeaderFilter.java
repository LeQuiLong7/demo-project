package com.lql.app.filter;

import com.lql.app.model.CustomAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReadHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String userId = request.getHeader("Id");
            String username = request.getHeader("Name");
            String permissions = request.getHeader("Permissions");
            List<SimpleGrantedAuthority> authorities = Arrays.stream(permissions.split(" "))
                    .parallel()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            SecurityContextHolder.getContext().setAuthentication(new CustomAuthentication(userId, username, authorities, true));

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(400);
            response.setContentType("text/plain");
            response.getWriter().println("Some thing went wrong");
        }

    }
}
