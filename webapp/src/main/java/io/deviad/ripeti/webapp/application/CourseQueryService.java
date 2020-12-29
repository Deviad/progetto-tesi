package io.deviad.ripeti.webapp.application;

import io.deviad.ripeti.webapp.adapter.UserAdapters;
import io.deviad.ripeti.webapp.api.queries.UserInfoDto;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
@Lazy
public class CourseQueryService {

        R2dbcEntityOperations client;

    @Timed("getAllEnrolledStudents")
    public Mono<UserInfoDto> getAllEnrolledStudents(UUID courseId) {

        //language=PostgreSQL
        String query =
                """
                SELECT u.* FROM unnest(array(select c.student_ids from courses c where c.id = $1)) user_id
                JOIN users u on u.id=user_id
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", courseId.toString())
                .map(UserAdapters.USERINFO_FROM_ROW_MAP::apply)
                .first();
    }

}
