package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface CourseRepository extends R2dbcRepository<CourseAggregate, UUID> {}
