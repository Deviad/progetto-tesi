package io.deviad.ripeti.webapp.application;

import io.deviad.ripeti.webapp.api.command.RegistrationRequest;
import io.deviad.ripeti.webapp.persistence.AddressEntity;
import io.deviad.ripeti.webapp.persistence.UserEntity;
import io.deviad.ripeti.webapp.persistence.repository.AddressRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class UserCommandService {

  private UserRepository userRepository;
  private AddressRepository addressesRepository;

  @Transactional
  public Mono<UserEntity> registerUser(RegistrationRequest r) {
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
}
