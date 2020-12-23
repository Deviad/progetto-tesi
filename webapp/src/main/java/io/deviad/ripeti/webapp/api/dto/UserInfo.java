package io.deviad.ripeti.webapp.api.dto;

import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import lombok.Value;

@Value(staticConstructor = "of")
public class UserInfo {

  String username;
  String firstName;
  String lastName;
  Address address;
}
