package org.triple.ClubMiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .select()
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Triple Swagger")
                .description("Triple swagger config\n" +
                        "- user01: 240a0658-dc5f-4878-9381-ebb7b2667772\n" +
                        "- user02: 3ede0ef2-92b7-4817-a5f3-0c575361f745\n\n" +
                        "- place01: 2e4baf1c-5acb-4efb-a1af-eddada31b00f\n" +
                        "- place02: afb0cef2-851d-4a50-bb07-9cc15cbdc332\n" +
                        "- place03: e4d1a64e-a531-46de-88d0-ff0ed70c0bb8")
                .version("1.0")
                .build();
    }
}