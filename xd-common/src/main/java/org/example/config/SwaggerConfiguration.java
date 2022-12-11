package org.example.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Component
@Data
@EnableOpenApi
public class SwaggerConfiguration {

    @Bean
    public Docket webApiDoc() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("clientside doc")
                .pathMapping("/")
                // if open swagger, false is close, can change by var, online close
                .enable(true)
                // config documentation info
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example"))
                // select all path under api
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("xd-shop-platform")
                .description("microservice interface doc")
                .contact(new Contact("Bob", "https://github.com/Chingpo-Lin", "ljb199992@gmail.com"))
                .version("v1.0")
                .build();
    }
}
