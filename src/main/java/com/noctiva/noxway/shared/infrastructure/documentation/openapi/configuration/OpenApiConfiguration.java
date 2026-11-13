package com.noctiva.noxway.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI specification configuration for Noxway Platform REST API.
 */
@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI noxwayOpenApi() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description(
                            "Structured IAM security scheme (currently deferred while app.security.enabled=false)"))
                .addSecuritySchemes(
                    "actorContextHeader",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Actor-Id")
                        .description(
                            "Optional actor UUID header used when IAM authentication enforcement is disabled")))
        .info(
            new Info()
                .title("Noxway Platform API")
                .version("v1")
                .description(
                    "REST API for night/day shift worker safe commuting, community intelligence, wellness logging, subscriptions, moderation, and trusted companion monitoring.")
                .contact(new Contact().name("Noctiva / Noxway Engineering"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
