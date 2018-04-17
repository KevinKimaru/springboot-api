package com.microfundit.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Kevin Kimaru Chege on 3/24/2018.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .tags(new Tag("Story Entity", "Repository for story entities"),
                        new Tag("Upload and Download Image", "Controller to download and upload images for a story."))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Lists.newArrayList(apiKey()))
                .apiInfo(apiInfo());
    }

    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration(
                "null",
                "null",
                "null",
                "null",
                "",
                ApiKeyVehicle.HEADER, "Authorization",
                ",");
//        return SecurityConfigurationBuilder.builder()
//                .clientId("test-app-client-id")
//                .clientSecret("test-app-client-secret")
//                .realm("test-app-realm")
//                .appName("test-app")
//                .scopeSeparator(",")
//                .build();
    }

    @Bean
    SecurityScheme apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Microfundit API")
                .description("This documentation exposes all the routes in the api. It should therefore" +
                        "only be used by administrators and authorised developers.")
                .contact(new Contact("microfundit", "https://microfundit.com", "microfundit@info.com"))
                .license("MIT")
                .termsOfServiceUrl("https://microfundit.com")
                .version("1.0.0")
                .build();
    }
}
