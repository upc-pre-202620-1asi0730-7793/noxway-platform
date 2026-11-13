package com.noctiva.noxway.iam.infrastructure.authorization.sfs.configuration;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Structural WebSecurity configuration reserved for when {@code app.security.enabled=true} is activated.
 * TODO: Register JWT / opaque bearer authentication filter once IAM enforcement is implemented.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true")
public class WebSecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      var configuration = new CorsConfiguration();
                      configuration.setAllowedOriginPatterns(List.of("*"));
                      configuration.setAllowedMethods(
                          List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                      configuration.setAllowedHeaders(List.of("*"));
                      return configuration;
                    }))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/v1/auth/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health")
                    .permitAll()
                    .anyRequest()
                    .authenticated());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
