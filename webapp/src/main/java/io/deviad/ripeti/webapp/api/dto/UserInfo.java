package io.deviad.ripeti.webapp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value(staticConstructor = "of")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInfo {
  String username;
  String email;
  String firstName;
  String lastName;
  Address address;
}
