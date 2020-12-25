package io.deviad.ripeti.webapp.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.api.dto.Address;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/*
   The following two lines are used to tell Jackson that
   getters/setters are not standard with prefix get/set.
*/
@Value
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RegistrationRequest {
  @NotBlank
  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  String username;

  @NotBlank
  @Pattern(
      regexp =
          "(?=.*[a-z]+)(?=.*[0-9]+)(?=.*[A-Z]+)(?=.*[!@#$%^&*()_+\\[\\]{}:\";,.<>?|=-_]+).{8,20}")
  String password;

  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  @NotBlank
  String firstName;

  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  @NotBlank
  String lastName;

  @Valid @NotNull Address address;
}
