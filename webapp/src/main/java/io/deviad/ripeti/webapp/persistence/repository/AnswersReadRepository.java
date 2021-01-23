package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.entity.AnswerEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface AnswersReadRepository extends ReactiveReadRepository<AnswerEntity, UUID> {


    //language=PostgreSQL
    @Query(value = """
                     select a.* from unnest(array(select qs.answer_ids from questions qs where qs.id::text = :questionId)) answer_id
                                          join answers a on a.id=answer_id;
            """
    )
    Flux<AnswerEntity> getAllAnswerEntitiesByQuestionId(String questionId);
}

