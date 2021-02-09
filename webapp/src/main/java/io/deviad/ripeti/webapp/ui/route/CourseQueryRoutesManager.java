package io.deviad.ripeti.webapp.ui.route;

import io.deviad.ripeti.webapp.application.query.CourseQueryService;
import io.deviad.ripeti.webapp.application.query.QuizQueryService;
import io.deviad.ripeti.webapp.ui.queries.CourseInfo;
import io.deviad.ripeti.webapp.ui.queries.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
@AllArgsConstructor
public class CourseQueryRoutesManager {

  CourseQueryService queryService;
  QuizQueryService quizQueryService;

  @RouterOperations({
    @RouterOperation(
        path = "/api/course/{courseId}",
        method = RequestMethod.GET,
        beanClass = CourseQueryService.class,
        beanMethod = "getCourseById",
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/{courseId}/getstudents",
        method = RequestMethod.GET,
        beanClass = CourseQueryService.class,
        beanMethod = "getAllEnrolledStudents",
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/teacher/{teacherId}",
        method = RequestMethod.GET,
        beanClass = CourseQueryService.class,
        beanMethod = "getCoursesByTeacherId",
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/{courseId}/getquizzes",
        method = RequestMethod.GET,
        beanClass = QuizQueryService.class,
        beanMethod = "getAllQuizzes",
        produces = MediaType.APPLICATION_JSON_VALUE)
  })
  @Bean
  public RouterFunction<ServerResponse> courseQueryRoutes() {
    return route()
        .GET("/api/course/{courseId}", this::handleGetCourse)
        .GET("/api/course/{courseId}/getstudents", this::handleGetEnrolledStudents)
        .GET("/api/course/teacher/{teacherId}", this::handleGetByTeacherId)
        .GET("/api/course/{courseId}/getquizzes", this::getQuizzesByCourseId)
        .build();
  }

  Mono<ServerResponse> handleGetCourse(ServerRequest request) {
    return Mono.just(request.pathVariable("courseId"))
        .onErrorResume(Mono::error)
        .flatMap(r -> queryService.getCourseById(r))
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot find course")))
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> handleGetEnrolledStudents(ServerRequest request) {
    return Mono.just(request.pathVariable("courseId"))
        .onErrorResume(Mono::error)
        .map(r -> queryService.getAllEnrolledStudents(r))
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot find students")))
        .flatMap(r -> ServerResponse.ok().body(r, UserInfoDto.class));
  }

  Mono<ServerResponse> handleGetByTeacherId(ServerRequest request) {
    return Mono.just(request.pathVariable("teacherId"))
        .onErrorResume(Mono::error)
        .map(r -> queryService.getCoursesByTeacherId(r))
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot find courses")))
        .flatMap(r -> ServerResponse.ok().body(r, CourseInfo.class));
  }

  Mono<ServerResponse> getQuizzesByCourseId(ServerRequest request) {
    return Mono.just(request.pathVariable("courseId"))
        .onErrorResume(Mono::error)
        .flatMap(r -> quizQueryService.getAllQuizzes(r))
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot find quizzes")))
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }
}
