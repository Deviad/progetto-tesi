package io.deviad.ripeti.webapp.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

public class MappingUtils {

  public static final ObjectMapper MAPPER =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  static {
    MAPPER.addMixIn(DataSize.class, DataSizeMixin.class);
  }

  public static ObjectMapper create() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .addMixIn(DataSize.class, DataSizeMixin.class);
  }

  /** Method to deserialize JSON content from given String into required type. */
  @SneakyThrows(IOException.class)
  public static <T> T fromJson(String jsonString, Class<T> toValueType) {
    return hasText(jsonString) ? MAPPER.readValue(jsonString, toValueType) : null;
  }

  /** Method to convert object data to json. */
  @SneakyThrows(JsonProcessingException.class)
  public static String toJson(Object data) {
    return data == null ? null : MAPPER.writeValueAsString(data);
  }

  /** Method to convert object data to Map. */
  public static Map<String, Object> toMap(Object data) {
    return MAPPER.convertValue(data, new TypeReference<>() {});
  }
}
