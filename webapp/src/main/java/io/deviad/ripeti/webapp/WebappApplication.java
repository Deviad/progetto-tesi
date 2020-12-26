package io.deviad.ripeti.webapp;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.tools.agent.ReactorDebugAgent;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@SpringBootApplication
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
  JvmThreadMetrics threadMetrics(){
    return new JvmThreadMetrics();
  }
  @Bean
  CompositeMeterRegistry compositeMeterRegistry() {
    CompositeMeterRegistry registry = new CompositeMeterRegistry();
    registry.add(new JmxMeterRegistry(s -> null, Clock.SYSTEM));
    return registry;
  }
}
