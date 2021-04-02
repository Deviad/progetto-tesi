package io.deviad.ripeti.webapp.application.query;

import io.deviad.ripeti.webapp.adapter.CourseAdapters;
import io.deviad.ripeti.webapp.adapter.UserAdapters;
import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import io.deviad.ripeti.webapp.ui.queries.AnswerQuery;
import io.deviad.ripeti.webapp.ui.queries.CompleteCourseInfo;
import io.deviad.ripeti.webapp.ui.queries.CourseInfo;
import io.deviad.ripeti.webapp.ui.queries.QuestionResponseDto;
import io.deviad.ripeti.webapp.ui.queries.QuizWithResults;
import io.deviad.ripeti.webapp.ui.queries.Student;
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


    @Timed("getAllCourseInfoByCourseId")
    public Mono<CompleteCourseInfo> getAllCourseInfoByCourseId(@Parameter(required = true, in = ParameterIn.PATH) String courseId) {
        String query =
                """
                    select * from f_get_complete_course_info('$1') as t(
                      course_id uuid,
                      course_name varchar,
                      course_description text,
                      course_status course_status,
                      course_teacher_id uuid,
                      quiz_name varchar,
                      quiz_content text,
                      quiz_id uuid,
                      teacher_full_name text,
                      teacher_email varchar,
                      student_email varchar,
                      student_full_name text,
                      student_username varchar,
                      lesson_name varchar,
                      lesson_content text,
                      lesson_id uuid,
                      question_title varchar,
                      question_id uuid,
                      answer_id uuid,
                      answer_title varchar,
                      answer_value bool); 
                """.replace("$1", courseId);
        return client.getDatabaseClient().sql(query)
                .map(CourseAdapters.COMPLETE_COURSEINFO_FROM_ROW_MAP::apply)
                .all()
                .collect(CompleteCourseInfo::new, (k, r)-> {
                   k.setCourseId(r.getCourseId());
                   k.setCourseName(r.getCourseName());
                   k.setCourseDescription(r.getCourseDescription());
                   k.setCourseStatus(r.getCourseStatus());
                   k.getStudentList().add(Student.builder()
                           .email(r.getStudentEmail())
                           .username(r.getStudentUsername())
                           .studentCompleteName(r.getStudentFullName())
                           .build());
                   k.setTeacherId(r.getCourseTeacherId());
                   k.setTeacherName(r.getTeacherName());
                   k.setTeacherEmail(r.getTeacherEmail());
                   if (r.getQuizId() != null) {
                       k.getQuizzes().put(r.getQuizId(), QuizWithResults
                               .builder()
                               .id(r.getQuizId())
                               .quizName(r.getQuizName())
                               .build());
                   }
                   if (r.getQuizId() != null
                           && r.getQuestionId() != null
                           && k.getQuizzes().get(r.getQuizId()) != null
                   ) {
                       k.getQuizzes().get(r.getQuizId()).questions().put(r.getQuestionId(),
                               QuestionResponseDto.builder()
                                       .id(r.getQuestionId())
                                       .title(r.getQuestionTitle())
                                       .build());
                   }
                    if (r.getQuizId() != null
                            && r.getQuestionId() != null
                            && r.getAnswerId() != null
                            && k.getQuizzes().get(r.getQuizId()) != null
                            && k.getQuizzes().get(r.getQuizId()).questions().get(r.getQuestionId()) != null

                    ) {
                        k.getQuizzes()
                                .get(r.getQuizId())
                                .questions()
                                .get(r.getQuestionId())
                                .answers()
                                .put(r.getAnswerId(), AnswerQuery
                                        .builder()
                                        .title(r.getAnswerTitle())
                                        .value(r.getAnswerValue())
                                        .build());
                    }
                });
    }

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
