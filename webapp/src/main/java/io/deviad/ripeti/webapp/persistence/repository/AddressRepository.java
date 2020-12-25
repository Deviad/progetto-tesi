package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.persistence.AddressEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface AddressRepository extends R2dbcRepository<AddressEntity, UUID> {}
