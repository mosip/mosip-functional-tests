package io.mosip.test.packetcreator.mosippacketcreator.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String SERVICE_VERSION = "0.0.1";
    private static final String TITLE = "Test Data Service";
    private static final String DESCRIPTION = "Test Data Service";


    /**
     * Produce Docket bean
     *
     * @return Docket bean
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .paths(PathSelectors.regex("(?!/(error).*).*")).build();
    }

    /**
     * Produces {@link ApiInfo}
     *
     * @return {@link ApiInfo}
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(TITLE).description(DESCRIPTION).version(SERVICE_VERSION).build();
    }

}
