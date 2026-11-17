package br.com.setassembler.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger UI em http://localhost:8080/swagger-ui.html — botão "Authorize" aceita o token JWT. */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Set-Assembler API", version = "1.0",
                description = "Montagem de setups com verificação de compatibilidade, CRUD de setups e relatório em PDF."),
        security = @SecurityRequirement(name = "bearer"))
@SecurityScheme(name = "bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
