package io.deviad.ripeti.webapp.ui.queries;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
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
  Address address;
}
