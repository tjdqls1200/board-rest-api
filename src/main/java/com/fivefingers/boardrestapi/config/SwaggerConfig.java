package com.fivefingers.boardrestapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.*;

@Configuration
public class SwaggerConfig {
    private static final Contact DEFAULT_CONCAT = new Contact(
            "SUNG-BIN",
            "http://www.treecode.com",
            "dltjdqls1200@naver.com");
    private static final ApiInfo DEFAULT_API_INFO = new ApiInfoBuilder()
            .title("TreeCode Swagger")
            .description("TreeCode REST API Board API")
            .version("1.0")
            .contact(DEFAULT_CONCAT)
            .build();
    private static final String DEFAULT_PRODUCES_AND_CONSUMES = "application/json";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .consumes(Collections.singleton(DEFAULT_PRODUCES_AND_CONSUMES))
                .produces(Collections.singleton(DEFAULT_PRODUCES_AND_CONSUMES))
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .apiInfo(DEFAULT_API_INFO)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }
}
