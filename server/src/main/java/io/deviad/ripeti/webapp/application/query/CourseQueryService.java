package io.deviad.ripeti.webapp.application.query;

import io.deviad.ripeti.webapp.adapter.CourseAdapters;
import io.deviad.ripeti.webapp.adapter.UserAdapters;
import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import io.deviad.ripeti.webapp.ui.queries.CourseInfo;
import io.deviad.ripeti.webapp.ui.queries.UserInfoDto;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Lazy
public class CourseQueryService {

        R2dbcEntityOperations client;

    @Timed("getAllEnrolledStudents")
    public Flux<UserInfoDto> getAllEnrolledStudents(@Parameter(required = true, in = ParameterIn.PATH) String courseId) {

        //language=PostgreSQL
        String query =
                """
                select u.* from unnest(array(select c.student_ids from courses c where c.id::text = $1)) user_id
                join users u on u.id=user_id
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", courseId)
                .map(UserAdapters.USERINFO_FROM_ROW_MAP::apply)
                .all();
    }

    @Timed("getCourseById")
    public Mono<CourseInfo> getCourseById(@Parameter(required = true, in = ParameterIn.PATH) String courseId) {
        //language=PostgreSQL
        String query =
                """
                select c.*, concat(u.first_name, ', ', u.last_name) as teacher_name from courses c
                join users u on c.teacher_id = u.id
                where c.id::text = $1
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", courseId)
                .map(CourseAdapters.COURSEINFO_FROM_ROW_MAP::apply)
                .first();
    }


    @Timed("getCourseByTeacherEmail")
    public Flux<CourseInfo> getCoursesByTeacherEmail(@Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken p) {
        //language=PostgreSQL
        String query =
                """
                 select c.*, concat(u.first_name, ', ', u.last_name) as teacher_name from courses c
                    join users u on c.teacher_id = u.id
                    where u.email = $1
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", p.getTokenAttributes().get("email"))
                .map(CourseAdapters.COURSEINFO_FROM_ROW_MAP::apply)
                .all();
    }

    @Timed("getAllCourses")
    public Flux<CourseInfo> getAllLiveCourses() {
        //language=PostgreSQL
        String query =
                """
                 select c.*, concat(u.first_name, ', ', u.last_name) as teacher_name from courses c
                    join users u on c.teacher_id = u.id
                    where c.status::text = $1
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", CourseStatus.LIVE.name())
                .map(CourseAdapters.COURSEINFO_FROM_ROW_MAP::apply)
                .all();
    }

    @Timed("getAllCoursesWhereStudentIsEnrolled")
    public Flux<CourseInfo> getAllCoursesWhereStudentIsEnrolled(
            @Parameter(
                    required = true,
                    in = ParameterIn.HEADER) JwtAuthenticationToken p) {
        //language=PostgreSQL
        String query =
                """
                select c.*, concat(u.first_name, ', ', u.last_name) as teacher_name from courses c
                    join users u on c.teacher_id = u.id
                    where (select id from users uu where uu.email = $1) = any(c.student_ids)
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", p.getTokenAttributes().get("email"))
                .map(CourseAdapters.COURSEINFO_FROM_ROW_MAP::apply)
                .all();
    }

}
