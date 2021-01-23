package io.deviad.ripeti.webapp.persistence.repository;


import io.deviad.ripeti.webapp.domain.entity.QuizEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface QuizQueryRepository extends ReactiveReadRepository<QuizEntity, UUID> {
    //language=PostgreSQL
    @Query(value = """
             select qi.* from unnest(array(select c.quiz_ids from courses c where c.id::text = :courseId)) quiz_id
             join quizzes qi on qi.id=quiz_id
            """

    )
    Flux<QuizEntity> getAllQuizEntitiesByCourseId(String courseId);

}





