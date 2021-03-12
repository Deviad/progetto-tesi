package io.deviad.ripeti.webapp.infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.deviad.ripeti.webapp.adapter.DataSizeMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class JacksonConfiguration {

  @Bean
  ObjectMapper objectMapper() {

      return new ObjectMapper()
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
              .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
              .addMixIn(DataSize.class, DataSizeMixin.class)
              .findAndRegisterModules();
  }
}
