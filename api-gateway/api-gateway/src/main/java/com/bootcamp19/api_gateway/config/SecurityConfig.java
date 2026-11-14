package com.bootcamp19.api_gateway.config;

import com.bootcamp19.api_gateway.util.JwtUtil;
import com.bootcamp19.api_gateway.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/product/internal/**").denyAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/product/**", "/scheduler/**").authenticated()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, err) ->
                                ResponseUtil.writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid JWT token"))
                        .accessDeniedHandler((exchange, denied) ->
                                ResponseUtil.writeError(exchange, HttpStatus.FORBIDDEN, "Access to internal endpoint is forbidden"))
                )
                .addFilterAt(new JwtAuthenticationFilter(jwtUtil), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}