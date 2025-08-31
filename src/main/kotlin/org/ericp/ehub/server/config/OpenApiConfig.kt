package org.ericp.ehub.server.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("My Hub API")
                    .version("1.0.0")
                    .description("Personal utilities hub API - All endpoints require API key authentication")
            )
            .components(
                Components()
                    .addSecuritySchemes("apiKey",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("X-API-Key")
                            .description("API Key for authentication")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("apiKey"))
}
