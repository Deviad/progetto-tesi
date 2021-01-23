package io.deviad.ripeti.webapp.persistence.repository;

import io.deviad.ripeti.webapp.domain.entity.QuestionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface QuestionReadRepository extends ReactiveReadRepository<QuestionEntity, UUID> {


    //language=PostgreSQL
    @Query(value = """
             select qs.* from unnest(array(select qi.question_ids from quizzes qi where qi.id::text = :quizId)) question_id
                                                                join questions qs on qs.id=question_id
            """
    )
    Flux<QuestionEntity> getAllQuestionEntitiesByQuizId(String quizId);


}

