package com.sb.filenet.genericapi.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String DEFAULT_INCLUDE_PATTERN = "/rest/api/.*";

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.sb.filenet.genericapi"))
                .paths(regex(DEFAULT_INCLUDE_PATTERN))
                .build().apiInfo(apiEndPointsInfo());
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .docExpansion(DocExpansion.LIST) // or DocExpansion.NONE or DocExpansion.FULL
                .build();
    }

    private ApiInfo apiEndPointsInfo() {

        return new ApiInfoBuilder().title("FileNet Generic REST API")
                //.description("Filenet Generic API Management for upload and retrieve the documents from filenet Content Manager.")
                .contact(new Contact("Standard  bank", "https://www.standardbank.co.za/", ""))
                .version("1.0-SNAPSHOT")
                .build();
    }

}
