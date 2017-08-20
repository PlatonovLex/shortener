package ru.platonov.shortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * SwaggerConfig.
 * <p>
 *     Swagger configuration
 * </p>
 *
 * @author Platonov Alexey
 * @since 19.08.2017
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurerAdapter {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .securitySchemes(Collections.singletonList(new BasicAuth("basicAuth")))
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.platonov.shortener.web"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo());
    }

    private static ApiInfo apiInfo() {
        return new ApiInfo(
                "Shortener API",
                getServiceDescription(),
                "1.0",
                "",
                new Contact("Platonov Alexey", "", "platonov.lex@gmail.com"),
                "License of API",
                "https://www.apache.org/licenses/LICENSE-2.0");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/swagger/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController(
                "/swagger/swagger-resources/configuration/ui",
                "/swagger-resources/configuration/ui");
        registry.addRedirectViewController(
                "/swagger/swagger-resources/configuration/security",
                "/swagger-resources/configuration/security");
        registry.addRedirectViewController("/swagger/swagger-resources", "/swagger-resources");
        registry.addRedirectViewController("/swagger", "/swagger/swagger-ui.html");
        registry.addRedirectViewController("/swagger/", "/swagger/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    private static String getServiceDescription() {
        return "SaaS service for user, who want to make their life easier! " +
                "To start using, register an account and just add a long link, Nothing is easier " +
                "You can read about the technologies used here : https://github.com/PlatonovLex/shortener/wiki";
    }

}
