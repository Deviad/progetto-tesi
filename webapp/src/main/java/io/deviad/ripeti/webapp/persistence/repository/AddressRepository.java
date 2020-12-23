package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.persistence.AddressEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface AddressRepository extends R2dbcRepository<AddressEntity, Long> {}
