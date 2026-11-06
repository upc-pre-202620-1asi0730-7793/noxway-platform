package com.noctiva.noxway.shared.infrastructure.configuration;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfiguration {
  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Noxway Platform API")
                .version("1.0.0")
                .description(
                    "Day and night shift workers and trusted contacts. External delivery and"
                        + " payments require providers."))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }
}
