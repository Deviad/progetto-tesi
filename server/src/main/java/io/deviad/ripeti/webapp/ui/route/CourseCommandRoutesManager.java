package io.deviad.ripeti.webapp.ui.route;

import io.deviad.ripeti.webapp.application.command.CourseCommandService;
import io.deviad.ripeti.webapp.application.command.LessonCommandService;
import io.deviad.ripeti.webapp.application.command.UserCommandService;
import io.deviad.ripeti.webapp.ui.Utils;
import io.deviad.ripeti.webapp.ui.command.create.AddLessonsToCourseRequestCommand;
import io.deviad.ripeti.webapp.ui.command.create.AddQuizToCourseCommand;
import io.deviad.ripeti.webapp.ui.command.create.CreateCourseRequest;
import io.deviad.ripeti.webapp.ui.command.update.UpdateCourseRequest;
import io.deviad.ripeti.webapp.ui.command.update.UpdateLessonsCommand;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
@AllArgsConstructor
public class CourseCommandRoutesManager {

  CourseCommandService courseService;
  UserCommandService userCommandService;
  LessonCommandService lessonCommandService;

  @RouterOperations({
    @RouterOperation(
        path = "/api/course",
        method = RequestMethod.POST,
        beanClass = CourseCommandService.class,
        beanMethod = "createCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course",
        method = RequestMethod.PUT,
        beanClass = CourseCommandService.class,
        beanMethod = "updateCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/{courseId}",
        method = RequestMethod.DELETE,
        beanClass = CourseCommandService.class,
        beanMethod = "deleteCourse"),
    @RouterOperation(
        path = "/api/course/{courseId}/publish/{teacherId}",
        method = RequestMethod.PUT,
        beanClass = CourseCommandService.class,
        beanMethod = "publishCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/{courseId}/assignstudent/{studentId}",
        method = RequestMethod.PUT,
        beanClass = CourseCommandService.class,
        beanMethod = "assignUserToCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/{courseId}/unassignstudent/{studentId}",
        method = RequestMethod.PUT,
        beanClass = CourseCommandService.class,
        beanMethod = "unassignUserFromCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/{courseId}/addlesson",
        method = RequestMethod.POST,
        beanClass = CourseCommandService.class,
        beanMethod = "addLessonToCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/removelesson/{lessonId}",
        method = RequestMethod.DELETE,
        beanClass = CourseCommandService.class,
        beanMethod = "removeLessonFromCourse"),
    @RouterOperation(
        path = "/api/course/{courseId}/createquiz",
        beanClass = CourseCommandService.class,
        method = RequestMethod.POST,
        beanMethod = "addQuizToCourse",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE),
    @RouterOperation(
        path = "/api/course/removequiz/{quizId}",
        beanClass = CourseCommandService.class,
        method = RequestMethod.DELETE,
        beanMethod = "removeQuizFromCourse"),
  })
  @Bean
  public RouterFunction<ServerResponse> courseCommandRoutes() {
    return route()
        .POST(
            "/api/course",
            RequestPredicates.contentType(MediaType.APPLICATION_JSON),
            this::createCourse)
        .build()
        .and(
            route()
                .PUT(
                    "/api/course/{courseId}",
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON),
                    this::handleUpdate)
                .build())
        .and(route().DELETE("/api/course/{courseId}", this::deleteCourse).build())
        .and(route().PUT("/api/course/{courseId}/publish/{teacherId}", this::publishCourse).build())
        .and(
            route()
                .PUT(
                    "/api/course/{courseId}/assignstudent/{studentId}", this::assignStudentToCourse)
                .build())
        .and(
            route()
                .PUT(
                    "/api/course/{courseId}/unassignstudent/{studentId}",
                    this::unassignUserFromCourse)
                .build())
        .and(
            route()
                .POST(
                    "/api/course/{courseId}/addlesson",
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON),
                    this::addLessonsToCourse)
                .build())
        .and(
            route()
                .PUT(
                    "/api/course/{courseId}/updateLessons",
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON),
                    this::updateLessons)
                .build())
        .and(route().DELETE("/api/course/removelesson/{lessonId}", this::removeLesson).build())
        .and(
            route()
                .POST(
                    "/api/course/{courseId}/createquiz",
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON),
                    this::createOrUpdateQuiz)
                .build()
                .and(route().DELETE("/api/course/removequiz/{quizId}", this::removeQuiz).build()));
  }

  Mono<ServerResponse> createCourse(ServerRequest request) {

    return Utils.fetchPrincipal(request)
        .flatMap(
            p ->
                Mono.zip(
                    Mono.just(p),
                    request.bodyToMono(CreateCourseRequest.class).onErrorResume(Mono::error)))
        .map(
            r -> {
              log.info("principal name is {}", r.getT1().getName());
              return courseService.createCourse(r.getT2(), (JwtAuthenticationToken) r.getT1());
            })
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> deleteCourse(ServerRequest request) {
    String courseId = request.pathVariable("id");
    return Utils.fetchPrincipal(request)
        .flatMap(
            p ->
                courseService
                    .deleteCourse(UUID.fromString(courseId), (JwtAuthenticationToken) p)
                    .onErrorResume(Mono::error))
        .flatMap(r -> ServerResponse.ok().build());
  }

  Mono<ServerResponse> handleUpdate(ServerRequest request) {
    String courseId = request.pathVariable("courseId");

    return Utils.fetchPrincipal(request)
        .flatMap(
            p ->
                Mono.zip(
                    Mono.just(p),
                    request.bodyToMono(UpdateCourseRequest.class).onErrorResume(Mono::error)))
        .map(
            t -> {
              log.info("principal name is {}", t.getT1().getName());
              return courseService.updateCourse(
                  UUID.fromString(courseId), t.getT2(), (JwtAuthenticationToken) t.getT1());
            })
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> assignStudentToCourse(ServerRequest request) {
    UUID courseId = UUID.fromString(request.pathVariable("courseId"));
    UUID studentId = UUID.fromString(request.pathVariable("studentId"));

    return courseService
        .assignStudentToCourse(studentId, courseId)
        .onErrorResume(Mono::error)
        .flatMap(r -> ServerResponse.ok().build());
  }

  Mono<ServerResponse> unassignUserFromCourse(ServerRequest request) {
    UUID courseId = UUID.fromString(request.pathVariable("courseId"));
    UUID studentId = UUID.fromString(request.pathVariable("studentId"));

    return courseService
        .unassignUserFromCourse(studentId, courseId)
        .onErrorResume(Mono::error)
        .flatMap(r -> ServerResponse.ok().build());
  }

  @SneakyThrows
  Mono<ServerResponse> publishCourse(ServerRequest request) {
    UUID courseId = UUID.fromString(request.pathVariable("courseId"));

    return Utils.fetchPrincipal(request)
        .flatMap(p -> courseService.publishCourse(courseId, (JwtAuthenticationToken) p))
        .flatMap(x -> ServerResponse.ok().bodyValue(x));
  }

  @SneakyThrows
  Mono<ServerResponse> addLessonsToCourse(ServerRequest request) {
    return courseService.addUpdateLessonHandler(request, AddLessonsToCourseRequestCommand.class);
  }

  @SneakyThrows
  Mono<ServerResponse> updateLessons(ServerRequest request) {
    return courseService.addUpdateLessonHandler(request, UpdateLessonsCommand.class);
  }

  @SneakyThrows
  Mono<ServerResponse> removeLesson(ServerRequest request) {
    UUID lessonId = UUID.fromString(request.pathVariable("lessonId"));

    return Utils.fetchPrincipal(request)
        .flatMap(
            p ->
                courseService
                    .removeLessonFromCourse(lessonId, (JwtAuthenticationToken) p)
                    .onErrorResume(Mono::error))
        .flatMap(x -> ServerResponse.ok().build());
  }

  Mono<ServerResponse> createOrUpdateQuiz(ServerRequest request) {
    UUID courseId = UUID.fromString(request.pathVariable("courseId"));

    return Utils.fetchPrincipal(request)
        .flatMap(
            p ->
                Mono.zip(
                    Mono.just(p),
                    request.bodyToMono(AddQuizToCourseCommand.class).onErrorResume(Mono::error)))
        .flatMap(
            t ->
                courseService.addQuizToCourse(
                    courseId, t.getT2(), (JwtAuthenticationToken) t.getT1()))
        .flatMap(x -> ServerResponse.ok().build());
  }

  @SneakyThrows
  Mono<ServerResponse> removeQuiz(ServerRequest request) {
    UUID quizId = UUID.fromString(request.pathVariable("quizId"));

    return Utils.fetchPrincipal(request)
        .flatMap(p -> courseService.removeQuizFromCourse(quizId, (JwtAuthenticationToken) p))
        .flatMap(x -> ServerResponse.ok().build());
  }
}
