package com.lql.app.config;

import com.lql.app.filter.ReadHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.
                    csrf(AbstractHttpConfigurer::disable)
                    .addFilterAfter(new ReadHeaderFilter(), WebAsyncManagerIntegrationFilter.class)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .logout(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .securityContext(AbstractHttpConfigurer::disable)
                    .headers(AbstractHttpConfigurer::disable)
                    .requestCache(AbstractHttpConfigurer::disable)
                    .anonymous(AbstractHttpConfigurer::disable)
                    .sessionManagement(AbstractHttpConfigurer::disable)
                    .exceptionHandling(AbstractHttpConfigurer::disable)
                    .build();

//        return http.
//                    csrf(AbstractHttpConfigurer::disable)
//                    .authorizeHttpRequests(request -> request
////                            .requestMatchers("/api/v1/**").hasRole("READ")
//                            .anyRequest().authenticated())
//                    .addFilterAfter(new ReadHeaderFilter(), AnonymousAuthenticationFilter.class)
//                    .build();

    }
}
