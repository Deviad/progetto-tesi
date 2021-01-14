package io.deviad.ripeti.webapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.deviad.ripeti.webapp.api.command.RegistrationRequest;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class Utils {
  @SneakyThrows
  public static void handleValidation(
      ObjectMapper mapper, Set<ConstraintViolation<RegistrationRequest>> violations) {
    if (!violations.isEmpty()) {
      LinkedHashMap<String, String> messageMap =
          violations.stream()
              .collect(
                  LinkedHashMap::new,
                  (m, e) -> m.put(e.getPropertyPath().toString(), e.getMessage()),
                  Map::putAll);
      String message = mapper.writeValueAsString(messageMap);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
  }
}
