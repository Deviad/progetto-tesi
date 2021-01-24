package io.deviad.ripeti.webapp;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.tools.agent.ReactorDebugAgent;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class WebappApplication {
  public static void main(String[] args) {
    ReactorDebugAgent.init();
    SpringApplication.run(WebappApplication.class, args);
  }

  @Bean
  Validator validator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }

  @Bean
  public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
    return new OpenAPI()
//            .components(new Components().addSecuritySchemes("oauth2",
//                    new SecurityScheme().type(SecurityScheme.Type.OAUTH2).scheme("oauth2")))
            .info(new Info().title("Ripeti API").version(appVersion)
                    .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }


  @Bean
  public GroupedOpenApi coursesOpenApi() {
    String[] paths = { "/api/course/**" };
    return GroupedOpenApi.builder().group("courses").pathsToMatch(paths)
            .build();
  }

  @Bean
  public GroupedOpenApi usersOpenApi() {
    String[] paths = { "/api/user/**" };
    return GroupedOpenApi.builder().group("users").pathsToMatch(paths)
            .build();
  }


}
