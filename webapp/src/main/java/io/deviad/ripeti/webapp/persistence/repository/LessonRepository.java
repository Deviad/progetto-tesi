package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.entity.LessonEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface LessonRepository extends R2dbcRepository<LessonEntity, UUID> {}
