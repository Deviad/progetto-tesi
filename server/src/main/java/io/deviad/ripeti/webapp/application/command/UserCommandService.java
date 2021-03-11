package io.deviad.ripeti.webapp.application.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectReader;
import io.deviad.ripeti.webapp.Utils;
import io.deviad.ripeti.webapp.adapter.MappingUtils;
import io.deviad.ripeti.webapp.adapter.UserAdapters;
import io.deviad.ripeti.webapp.api.KeycloakAdminClient;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import io.deviad.ripeti.webapp.ui.command.RegistrationRequest;
import io.deviad.ripeti.webapp.ui.command.UpdatePasswordRequest;
import io.deviad.ripeti.webapp.ui.command.UpdateUserRequest;
import io.deviad.ripeti.webapp.ui.queries.UserInfoDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.UserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.Validator;
import java.util.Optional;

import static io.deviad.ripeti.webapp.adapter.UserAdapters.mapToUserInfo;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class UserCommandService {

  private UserRepository userRepository;
  private Validator validator;
  private KeycloakAdminClient client;
  private Common common;

  @Transactional
  @SneakyThrows
  public Mono<UserInfoDto> registerUser(@RequestBody(required = true) RegistrationRequest r) {

    Utils.handleValidation(MappingUtils.MAPPER, validator, r);

    var address =
        Address.builder()
            .firstAddressLine(r.address().firstAddressLine())
            .secondAddressLine(r.address().secondAddressLine())
            .city(r.address().city())
            .country(r.address().country())
            .build();

    return userRepository
        .getUserAggregateByUsername(r.username())
        .onErrorResume(Mono::error)
        .flatMap(
            x -> {
              if (x != null) {
                return Mono.error(
                    () ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists"));
              }
              return Mono.empty();
            })
        // When token == null it does not trigger flatmap
        // therefore we return Optional.empty()
        .defaultIfEmpty(Optional.empty())
        .then(client.save(r))
        .onErrorResume(Mono::error)
        .then(
            userRepository.save(
                UserAggregate.builder()
                    .username(r.username())
                    .password(r.password())
                    .email(r.email())
                    .firstName(r.firstName())
                    .lastName(r.lastName())
                    .role(r.role())
                    .address(address)
                    .build()))
        .onErrorResume(Mono::error)
        .flatMap(u -> Mono.just(UserAdapters.mapToUserInfo(u)));
  }


  public Mono<Void> logoutUser(JwtAuthenticationToken token) {
    final String email = common.getEmailFromToken(token);
    return client.logout(email);

  }

  @Transactional
  @SneakyThrows
  public Mono<UserInfoDto> updateUser(
      @RequestBody(required = true) UpdateUserRequest r, JwtAuthenticationToken token) {

    final String email = common.getEmailFromToken(token);

    var userEntity =
        userRepository
            .getUserAggregateByEmail(email)
            .switchIfEmpty(
                Mono.error(
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist")))
            .onErrorResume(Mono::error);

    return userEntity
        .flatMap(
            x ->
                Mono.zip(
                    handleUpdate(r, x).onErrorResume(Mono::error),
                    client.update(email, r).onErrorResume(Mono::error)))
        .map(Tuple2::getT1);
  }


  Mono<UserInfoDto> handleUpdate(UpdateUserRequest r, UserAggregate userEntity) {
    return Mono.just(userEntity)
        .flatMap(
            x -> {
              final ObjectReader objectReader = MappingUtils.MAPPER.readerForUpdating(x);
              return Mono.just(
                      API
                      .unchecked(()-> objectReader.readValue(writeCurrentEntityValues(r), UserAggregate.class))
                      .get()).onErrorResume(Mono::error);
            })
            .flatMap(x -> userRepository.save(x).onErrorResume(Mono::error))
            .flatMap(t -> Mono.just(mapToUserInfo(t)).onErrorResume(Mono::error));
  }
  @SneakyThrows
  private String writeCurrentEntityValues(UpdateUserRequest u) {
    return MappingUtils.create().setSerializationInclusion(JsonInclude.Include.NON_EMPTY).writeValueAsString(u);
  }

  @Transactional
  public Mono<UserAggregate> updatePassword(
      @RequestBody(required = true) UpdatePasswordRequest r,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final String email = common.getEmailFromToken(token);

    var userEntity =
        userRepository
            .getUserAggregateByEmail(email)
            .onErrorResume(Mono::error)
            .switchIfEmpty(
                Mono.error(
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist")));

    return userEntity
        .flatMap(
            x ->
                Mono.zip(
                    savePassword(r, userEntity),
                    client.updatePassword(email, r).onErrorResume(Mono::error)))
        .map(Tuple2::getT1);
  }

  Mono<UserAggregate> savePassword(UpdatePasswordRequest r, Mono<UserAggregate> userEntity) {
    return userEntity
        .map(x -> x.withPassword(r.password()))
        .map(x -> userRepository.save(x).onErrorResume(Mono::error).subscribe())
        .then(Mono.empty());
  }

  @Transactional
  public Mono<Void> deleteUser(
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final String email = common.getEmailFromToken(token);
    var userEntity = common.getUserByEmail(email);

    return userEntity.map(x -> userRepository.delete(x).subscribe()).then(Mono.empty());
  }
}
