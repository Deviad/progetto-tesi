package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;
@Tag(name="Courses")
public interface CourseRepository extends R2dbcRepository<CourseAggregate, UUID> {}
