package io.deviad.ripeti.webapp.application;

import com.fasterxml.jackson.core.type.TypeReference;
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

      if(!violations.isEmpty()) {
          LinkedHashMap<String, String> messageMap = violations.stream()
                  .collect(LinkedHashMap::new, (m, e) -> m.put(e.getPropertyPath().toString(), e.getMessage()), Map::putAll);
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

    Map<String, Object> requestMap = mapper.convertValue(r, new TypeReference<>() {});

    assert userEntity != null;

    return userEntity
        .map(
            x -> {
              x.setPassword(r.password());
              x.setFirstName(r.firstName());
              x.setLastName(r.lastName());
              return x;
            })
        .flatMap(x -> userRepository.save(x));

    //    AddressEntity addressEntity = mapper.convertValue(r, AddressEntity.class);

    //    Mono<AddressEntity> address =
    //            addressesRepository.save(
    //                    new AddressEntity(
    //                            null,
    //                            r.address().firstAddressLine(),
    //                            r.address().secondAddressLine(),
    //                            r.address().country(),
    //                            r.address().city()));

    //    return Mono.just(new UserEntity());

    //    return address.flatMap(
    //            ad ->
    //                    userRepository.save(
    //                            new UserEntity(
    //                                    null, r.username(), r.password(), r.firstName(),
    // r.lastName(), ad.getId())));

  }
}
