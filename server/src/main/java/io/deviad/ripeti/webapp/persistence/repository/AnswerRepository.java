package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.entity.AnswerEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

@Tag(name = "Answers")
public interface AnswerRepository extends R2dbcRepository<AnswerEntity, UUID> {}
