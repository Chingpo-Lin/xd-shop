package org.example.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger config
 * http://localhost:9001/swagger-ui/index.html#/
 */
@Component
@Data
@EnableOpenApi
public class SwaggerConfiguration {

    /**
     * for client
     * @return
     */
    @Bean
    public Docket webApiDoc() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("client side doc")
                .pathMapping("/")
                // if open swagger, false is close, can change by var, online close
                .enable(true)
                // config documentation info
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example"))
                // select all path under api
                .paths(PathSelectors.ant("/api/**"))
                .build()
                // swagger 3.0 config
                .globalRequestParameters(globalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResponseMsg())
                .globalResponses(HttpMethod.POST, getGlobalResponseMsg());
    }

    /**
     * for admin
     * @return
     */
    @Bean
    public Docket adminApiDoc() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("admin side doc")
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

    /**
     * config global param
     * @return
     */
    private List<RequestParameter> globalRequestParameters() {

        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("token")
                .description("login token")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        return parameters;
    }

    /**
     * generate respond info
     */
    private List<Response> getGlobalResponseMsg() {
        List<Response> list = new ArrayList<>();
        list.add(new ResponseBuilder().code("4xx")
                .description("request error, please check").build());

        return list;
    }
}
