package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.persistence.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    Mono<UserEntity> getUserEntityByUsername(String Username);

}
