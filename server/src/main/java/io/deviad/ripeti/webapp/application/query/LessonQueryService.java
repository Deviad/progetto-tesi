package io.deviad.ripeti.webapp.application.query;


import io.deviad.ripeti.webapp.ui.queries.Lesson;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static io.deviad.ripeti.webapp.adapter.LessonAdapters.LESSON_FROM_ROW_MAP;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class LessonQueryService {
    R2dbcEntityOperations client;

    @Timed("getAllLessonsByCourseId")
    public Mono<Map<UUID, Lesson>> getAllLessonsByCourseId(@Parameter(in = ParameterIn.PATH, required = true) String courseId) {

        String query =
                """
                select l.* from unnest(array(select c.lesson_ids from courses c where c.id::text = $1)) lesson_id
                join lessons l on l.id= lesson_id
                """;
        return client.getDatabaseClient().sql(query)
                .bind("$1", courseId)
                .map(LESSON_FROM_ROW_MAP::apply)
                .all()
                .collectMap(Lesson::id);

    }

}
