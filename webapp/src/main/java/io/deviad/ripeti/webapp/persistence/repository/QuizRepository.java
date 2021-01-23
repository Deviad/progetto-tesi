package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.entity.QuizEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface QuizRepository extends R2dbcRepository<QuizEntity, UUID> {}
;
