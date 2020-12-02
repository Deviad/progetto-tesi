package io.deviad.ripeti.webapp.domain;

import java.time.Instant;
import java.util.UUID;

public interface GenericRepository<T> {

     T save(T aggregate) ;

     T getByUUID(UUID uuid);

     T getByUUIDat(UUID uuid, Instant at);

}
