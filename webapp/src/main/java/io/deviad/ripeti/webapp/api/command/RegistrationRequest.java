package io.deviad.ripeti.webapp.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.api.dto.AddressDto;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
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

  /*
    General Email Regex (RFC 5322 Official Standard)
   */
  @Pattern(
      regexp="(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
  @NotBlank
  String email;

  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  @NotBlank
  String firstName;

  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  @NotBlank
  String lastName;

  @Valid @NotNull AddressDto address;

  @NotNull
  Role role;

}
