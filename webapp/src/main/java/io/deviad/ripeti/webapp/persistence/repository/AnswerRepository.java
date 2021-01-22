package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.entity.AnswerEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface AnswerRepository extends R2dbcRepository<AnswerEntity, UUID> {}

