package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Tag(name="Users")

public interface UserRepository extends R2dbcRepository<UserAggregate, UUID> {

  Mono<UserAggregate> getUserAggregateByUsername(String Username);
}
