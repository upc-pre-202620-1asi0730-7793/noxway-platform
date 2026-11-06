package com.noctiva.noxway.shared.infrastructure.security;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
public class SecurityConfiguration {
  @Bean
  org.springframework.boot.web.servlet.FilterRegistrationBean<SessionFilter> sessionRegistration(
      SessionFilter f) {
    var r = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(f);
    r.setEnabled(false);
    return r;
  }

  @Bean
  org.springframework.boot.web.servlet.FilterRegistrationBean<AuthRateLimitFilter>
      limiterRegistration(AuthRateLimitFilter f) {
    var r = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(f);
    r.setEnabled(false);
    return r;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http, SessionFilter filter, AuthRateLimitFilter limiter)
      throws Exception {
    return http.csrf(c -> c.disable())
        .cors(c -> {})
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers(
                        "/api/v1/auth/register",
                        "/api/v1/auth/login",
                        "/api/v1/auth/password-reset/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (r, s, x) -> {
                          s.setStatus(401);
                          s.setContentType("application/problem+json");
                          s.getWriter()
                              .write("{\"status\":401,\"detail\":\"Authentication required\"}");
                        })
                    .accessDeniedHandler(
                        (r, s, x) -> {
                          s.setStatus(403);
                          s.setContentType("application/problem+json");
                          s.getWriter().write("{\"status\":403,\"detail\":\"Access denied\"}");
                        }))
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(limiter, SessionFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource cors(@Value("${noxway.cors-origins}") String origins) {
    var c = new CorsConfiguration();
    c.setAllowedOrigins(Arrays.asList(origins.split(",")));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    c.setAllowCredentials(false);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", c);
    return source;
  }
}
