package io.deviad.ripeti.webapp.api;

import io.deviad.ripeti.webapp.adapter.UserRequestMapper;
import io.deviad.ripeti.webapp.ui.command.RegistrationRequest;
import io.deviad.ripeti.webapp.ui.command.UpdatePasswordRequest;
import io.deviad.ripeti.webapp.ui.command.UpdateUserRequest;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class KeycloakAdminClient {

  private UserRequestMapper mapper;
  private @Value("${keycloak.serverUrl}") String serverUrl;
  private @Value("${keycloak.realm}") String realm;
  private @Value("${keycloak.clientId}") String clientId;
  private @Value("${keycloak.clientSecret}") String clientSecret;
  private Keycloak keycloak;
  private RealmResource realmResource;

  @PostConstruct
  void init() {
    mapper = new UserRequestMapper();
    this.keycloak =
        KeycloakBuilder.builder() //
            .serverUrl(serverUrl) //
            .realm(realm) //
            .grantType(OAuth2Constants.PASSWORD) //
            .clientId(clientId) //
            .clientSecret(clientSecret) //
            .username("agrilinkadmin") //
            .password("password") //
            .build();

    this.realmResource = keycloak.realm(realm);
  }

  public Mono<Object> save(RegistrationRequest registrationRequest) {
    // Define user
    final UserRepresentation userRepresentation =
        mapper.mapToUserRepresentation(registrationRequest);

    // Get realm
    UsersResource usersRessource = realmResource.users();

    // Create user (requires manage-users role)
    Response response = usersRessource.create(userRepresentation);

    String userId = CreatedResponseUtil.getCreatedId(response);

    // Define password credential
    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(registrationRequest.password());

    UserResource userResource = usersRessource.get(userId);

    // Set password credential
    userResource.resetPassword(passwordCred);
    return Mono.empty();
  }

  public Mono<Object> delete(String userEmail) {
    realmResource = keycloak.realm(realm);
    UsersResource usersResource = realmResource.users();

    final List<UserRepresentation> search = usersResource.search(userEmail);
    if (search.isEmpty()) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find user"));
    }

    final UserRepresentation utilizator = search.get(0);
    UserResource userResource =
        Try.ofSupplier(() -> usersResource.get(utilizator.getId()))
            .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new);
    userResource.remove();
    return Mono.empty();
  }

  public Mono<Object> update(String email, UpdateUserRequest updateUserRequest) {
    RealmResource realmResource = keycloak.realm(realm);
    UsersResource usersResource = realmResource.users();

    final List<UserRepresentation> search = usersResource.search(null, null, null, email, 0, 1);
    if (search.isEmpty()) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keycloak: Cannot find user"));
    }

    final UserRepresentation utilizator = search.get(0);
    UserResource userResource =
        Try.ofSupplier(() -> usersResource.get(utilizator.getId()))
            .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new);

    userResource.update(mapper.mapToUserRepresentation(utilizator, updateUserRequest));
    return Mono.empty();
  }

  public Mono<Object> updatePassword(String email, UpdatePasswordRequest updateUserRequest) {
    RealmResource realmResource = keycloak.realm(realm);
    UsersResource usersResource = realmResource.users();

    final List<UserRepresentation> search = usersResource.search(email);
    if (search.isEmpty()) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find user"));
    }

    final UserRepresentation utilizator = search.get(0);
    UserResource userResource =
        Try.ofSupplier(() -> usersResource.get(utilizator.getId()))
            .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new);

    // Define password credential
    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(updateUserRequest.password());

    // Set password credential
    userResource.resetPassword(passwordCred);

    userResource.update(utilizator);
    return Mono.empty();
  }
}
