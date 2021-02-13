package io.deviad.ripeti.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@SpringBootApplication
@EnableWebFlux
public class ClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }

  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
      "classpath:/META-INF/resources/",
      "classpath:/resources/",
      "classpath:/static/",
      "classpath:/public/",
      "classpath:/react/"
  };


  @Bean
  WebFluxConfigurer webFluxConfigurer() {
    return new WebFluxConfigurer() {
      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/**")
            .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
            .resourceChain(true);
      }
    };
  }
}
