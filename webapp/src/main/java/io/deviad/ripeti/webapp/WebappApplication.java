package io.deviad.ripeti.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@SpringBootApplication
public class WebappApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebappApplication.class, args);
  }
  @Bean
  Validator validator() {
     ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }

}
