package io.deviad.ripeti.webapp.application.command;

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
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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

  @Transactional
  @SneakyThrows
  public Mono<UserInfoDto> registerUser(RegistrationRequest r) {

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
                return Mono.error(() -> new Throwable("User already exists"));
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

  @Transactional
  @SneakyThrows
  public Mono<UserInfoDto> updateUser(UpdateUserRequest r) {

    var userEntity = userRepository.getUserAggregateByUsername(r.username());
    return userEntity
        .onErrorResume(Mono::error)
        .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")))
        .map(x -> (Object) Mono.just(r.address()))
        .switchIfEmpty(saveWithoutAddress(r, userEntity))
        .flatMap(x -> saveWithAddress(r, userEntity));
  }

  Mono<UserInfoDto> saveWithoutAddress(UpdateUserRequest r, Mono<UserAggregate> userEntity) {
    return userEntity
        .map(x -> x.withFirstName(r.firstName()).withLastName(r.lastName()))
        .flatMap(x -> userRepository.save(x).onErrorResume(Mono::error))
        .map(
            x ->
                UserInfoDto.of(
                    x.username(), x.email(), x.firstName(), x.lastName(), x.role(), null));
  }

  Mono<UserInfoDto> saveWithAddress(UpdateUserRequest r, Mono<UserAggregate> userEntity) {
    return userEntity
        .onErrorResume(Mono::error)
        .map(
            x ->
                x.withFirstName(r.firstName())
                    .withLastName(r.lastName())
                    .withEmail(r.email())
                    .withAddress(
                        Address.builder()
                            .firstAddressLine(r.address().firstAddressLine())
                            .secondAddressLine(r.address().secondAddressLine())
                            .build()))
        .flatMap(x -> userRepository.save(x).onErrorResume(Mono::error))
        .flatMap((t) -> Mono.just(mapToUserInfo(t)));
  }

  @Transactional
  public Mono<UserAggregate> updatePassword(UpdatePasswordRequest r) {
    var userEntity = userRepository.getUserAggregateByUsername(r.username());

    return userEntity
        .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")))
        .flatMap(x -> savePassword(r, userEntity));
  }

  Mono<UserAggregate> savePassword(UpdatePasswordRequest r, Mono<UserAggregate> userEntity) {
    return userEntity.map(x -> x.withPassword(r.password())).flatMap(x -> userRepository.save(x));
  }
}
