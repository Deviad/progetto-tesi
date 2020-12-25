package io.deviad.ripeti.webapp.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.api.dto.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UpdateRequest {
  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  String username;

  @Pattern(
      regexp =
          "(?=.*[a-z]+)(?=.*[0-9]+)(?=.*[A-Z]+)(?=.*[!@#$%^&*()_+\\[\\]{}:\";,.<>?|=-_]+).{8,20}")
  String password;

  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  String firstName;

  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  String lastName;

  @Valid Address address;
}
