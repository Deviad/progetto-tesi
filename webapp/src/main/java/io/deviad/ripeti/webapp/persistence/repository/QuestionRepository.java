package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.entity.QuestionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface QuestionRepository extends R2dbcRepository<QuestionEntity, UUID> {}
