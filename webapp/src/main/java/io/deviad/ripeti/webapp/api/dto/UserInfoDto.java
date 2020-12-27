package io.deviad.ripeti.webapp.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import lombok.Value;

@Value(staticConstructor = "of")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInfoDto {
  String username;
  String email;
  String firstName;
  String lastName;
  Role role;
  AddressDto address;
}
