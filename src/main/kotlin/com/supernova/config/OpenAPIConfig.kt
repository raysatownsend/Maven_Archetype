package com.supernova.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("RESTful API with Kotlin 1.9.22 and SpringBoot 3.2.0")
                    .version("v1")
                    .description("About your API")
                    .termsOfService("")
                    .license(
                        License().name("Apache 4.0")
                            .url("")
                    )
            )

    }
}
