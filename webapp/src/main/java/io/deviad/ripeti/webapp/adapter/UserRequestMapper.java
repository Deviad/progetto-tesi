package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.ui.command.RegistrationRequest;
import lombok.NoArgsConstructor;
import org.keycloak.common.util.Time;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class UserRequestMapper {

  public UserRepresentation mapToUserRepresentation(RegistrationRequest registrationRequest) {
    if (registrationRequest == null) {
      return null;
    }
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setFirstName(registrationRequest.firstName());
    userRepresentation.setLastName(registrationRequest.lastName());
    userRepresentation.setEmail(registrationRequest.email());
    userRepresentation.setUsername(registrationRequest.username());
    userRepresentation.setEmailVerified(false);
    userRepresentation.setAttributes(getAttributes());
    userRepresentation.setEnabled(true);
    userRepresentation.setCreatedTimestamp(getCreatedTimestamp());
    if (userRepresentation.getApplicationRoles() != null) {
      Map<String, List<String>> map = getApplicationRoles(registrationRequest.role());
      if (map != null) {
        userRepresentation.getApplicationRoles().putAll(map);
      }
    }

    return userRepresentation;
  }

  Map<String, List<String>> getAttributes() {
    return Collections.singletonMap("locale", Collections.singletonList("en"));
  }

  long getCreatedTimestamp() {
    return Time.currentTimeMillis();
  }

  Map<String, List<String>> getApplicationRoles(Role role) {
    return Collections.singletonMap("agrilink-web", List.of(role.name()));
  }
}
