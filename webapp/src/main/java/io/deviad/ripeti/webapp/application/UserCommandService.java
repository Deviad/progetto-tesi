package io.deviad.ripeti.webapp.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.deviad.ripeti.webapp.api.command.RegistrationRequest;
import io.deviad.ripeti.webapp.api.command.UpdateRequest;
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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
  public Mono<UserEntity> registerUser(RegistrationRequest r) {
    Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(r);

    if (!violations.isEmpty()) {
      LinkedHashMap<String, String> messageMap =
          violations.stream()
              .collect(
                  LinkedHashMap::new,
                  (m, e) -> m.put(e.getPropertyPath().toString(), e.getMessage()),
                  Map::putAll);
      String message = mapper.writeValueAsString(messageMap);
      throw new RuntimeException(message);
    }

    Mono<AddressEntity> address =
        addressesRepository.save(
            new AddressEntity(
                null,
                r.address().firstAddressLine(),
                r.address().secondAddressLine(),
                r.address().country(),
                r.address().city()));
    return address.flatMap(
        ad ->
            userRepository.save(
                new UserEntity(
                    null, r.username(), r.password(), r.firstName(), r.lastName(), ad.getId())));
  }

  @Transactional
  @SneakyThrows
  public Mono<UserEntity> updateUser(UpdateRequest r) {

    var userEntity = userRepository.getUserEntityByUsername(r.username());

   return userEntity
            .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")))
            .map(x-> (Object)Mono.just(r.address()))
            .switchIfEmpty(saveWithoutAddress(r, userEntity))
            .flatMap(x->saveWithAddress(r, userEntity));
  }

   Mono<UserEntity> saveWithoutAddress(UpdateRequest r, Mono<UserEntity> userEntity) {
   return userEntity
        .map(
            x -> x.withPassword(r.password())
            .withFirstName(r.firstName())
            .withLastName(r.lastName()))
        .flatMap(x -> userRepository.save(x));
  }

   Mono<UserEntity> saveWithAddress(UpdateRequest r, Mono<UserEntity> userEntity) {
    return userEntity
        .map(
            x -> x.withPassword(r.password())
                    .withFirstName(r.firstName())
                    .withLastName(r.lastName()))
        .flatMap(x -> userRepository.save(x))
        .flatMap(x -> Mono.zip(Mono.just(x), addressesRepository.findById(x.addressId())))
        .flatMap(
            x -> {
              var address =
                  x.getT2()
                      .withFirstAddressLine(r.address().firstAddressLine())
                      .withSecondAddressLine(r.address().secondAddressLine())
                      .withCity(r.address().city())
                      .withCountry(r.address().country());
              addressesRepository.save(address);
              return Mono.just(x.getT1());
            });
  }
}

