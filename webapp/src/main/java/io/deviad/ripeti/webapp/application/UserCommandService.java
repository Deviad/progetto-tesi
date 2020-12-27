package io.deviad.ripeti.webapp.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.deviad.ripeti.webapp.Utils;
import io.deviad.ripeti.webapp.api.command.RegistrationRequest;
import io.deviad.ripeti.webapp.api.command.UpdatePasswordRequest;
import io.deviad.ripeti.webapp.api.command.UpdateRequest;
import io.deviad.ripeti.webapp.api.dto.UserInfoDto;
import io.deviad.ripeti.webapp.persistence.AddressEntity;
import io.deviad.ripeti.webapp.persistence.UserEntity;
import io.deviad.ripeti.webapp.persistence.repository.AddressRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static io.deviad.ripeti.webapp.adapter.UserAdapters.mapToUserInfo;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class UserCommandService {

  private UserRepository userRepository;
  private AddressRepository addressesRepository;
  private ObjectMapper mapper;
  private Validator validator;

  @Transactional
  @SneakyThrows
  public Mono<UserInfoDto> registerUser(RegistrationRequest r) {
    Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(r);

    Utils.handleValidation(mapper, violations);

    Function<RegistrationRequest, Mono<AddressEntity>> address =
        rr ->
            addressesRepository.save(
                new AddressEntity(
                    null,
                    rr.address().firstAddressLine(),
                    rr.address().secondAddressLine(),
                    rr.address().country(),
                    rr.address().city()));

    return userRepository
        .getUserEntityByUsername(r.username())
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
        .flatMap(
            x -> address.apply(r))
        .flatMap(saveEnrolledUser(r))
        .flatMap(t -> Mono.just(mapToUserInfo(t)));
  }

  private Function<AddressEntity, Mono<? extends Tuple2<UserEntity, AddressEntity>>>
      saveEnrolledUser(RegistrationRequest r) {
    return ad -> {
      Mono<UserEntity> user =
          userRepository.save(UserEntity.builder()
              .id(null)
              .username(r.username())
              .password(r.password())
              .email(r.email())
              .firstName(r.firstName())
              .lastName(r.lastName())
              .role(r.role())
              .addressId(ad.getId()).build());
      return Mono.zip(user, Mono.just(ad));
    };
  }

  @Transactional
  @SneakyThrows
  public Mono<UserInfoDto> updateUser(UpdateRequest r) {

    var userEntity = userRepository.getUserEntityByUsername(r.username());

    return userEntity
        .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")))
        .map(x -> (Object) Mono.just(r.address()))
        .switchIfEmpty(saveWithoutAddress(r, userEntity))
        .flatMap(x -> saveWithAddress(r, userEntity));
  }

  Mono<UserInfoDto> saveWithoutAddress(UpdateRequest r, Mono<UserEntity> userEntity) {
    return userEntity
        .map(x -> x.withFirstName(r.firstName()).withLastName(r.lastName()))
        .flatMap(x -> userRepository.save(x))
        .map(x -> UserInfoDto.of(x.username(), x.email(), x.firstName(), x.lastName(), x.role(), null));
  }

  Mono<UserInfoDto> saveWithAddress(UpdateRequest r, Mono<UserEntity> userEntity) {
    return userEntity
        .map(x -> x.withFirstName(r.firstName()).withLastName(r.lastName()).withEmail(r.email()))
        .flatMap(x -> userRepository.save(x))
        .flatMap(x -> Mono.zip(Mono.just(x), addressesRepository.findById(x.addressId())))
        .flatMap(
            x -> {
              var address = createNewAddress(r, x);
              Mono<AddressEntity> aEntity = addressesRepository.save(address);
              return Mono.zip(Mono.just(x.getT1()), aEntity);
            })
        .flatMap((t) -> Mono.just(mapToUserInfo(t)));
  }

  private AddressEntity createNewAddress(UpdateRequest r, Tuple2<UserEntity, AddressEntity> t) {
    return t.getT2()
        .withFirstAddressLine(r.address().firstAddressLine())
        .withSecondAddressLine(r.address().secondAddressLine())
        .withCity(r.address().city())
        .withCountry(r.address().country());
  }

  @Transactional
  public Mono<UserEntity> updatePassword(UpdatePasswordRequest r) {
    var userEntity = userRepository.getUserEntityByUsername(r.username());

    return userEntity
        .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")))
        .flatMap(x -> savePassword(r, userEntity));
  }

  Mono<UserEntity> savePassword(UpdatePasswordRequest r, Mono<UserEntity> userEntity) {
    return userEntity.map(x -> x.withPassword(r.password())).flatMap(x -> userRepository.save(x));
  }
}
