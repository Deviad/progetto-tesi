package io.deviad.ripeti.webapp.api.route;

import io.deviad.ripeti.webapp.api.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.api.command.UpdateCourseRequest;
import io.deviad.ripeti.webapp.application.CourseCommandService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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

  @Bean
  public RouterFunction<ServerResponse> courseCommandRoutes() {
    return route()
        .POST(
            "/api/course",
            RequestPredicates.contentType(MediaType.APPLICATION_JSON),
            this::createCourse)
        .PUT(
            "/api/course",
            RequestPredicates.contentType(MediaType.APPLICATION_JSON),
            this::handleUpdate)
        .DELETE("/api/course", this::deleteCourse)
        .PUT("/api/course/assign/{courseId}/{studentId}", this::assignStudentToCourse)
        .build();
  }

  Mono<ServerResponse> createCourse(ServerRequest request) {
    return request
        .bodyToMono(CreateCourseRequest.class)
        .onErrorResume(Mono::error)
        .map(r -> courseService.createCourse(r))
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> deleteCourse(ServerRequest request) {
    String courseId = request.pathVariable("id");

    Mono<ServerResponse> deleteMono =
        courseService
            .deleteCourse(UUID.fromString(courseId))
            .onErrorResume(Mono::error)
            .flatMap(r -> ServerResponse.ok().build());

    return courseService
        .getCourseById(UUID.fromString(courseId))
        .onErrorResume(Mono::error)
        .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")))
        .thenReturn(deleteMono)
        .flatMap(Function.identity());
  }

  Mono<ServerResponse> handleUpdate(ServerRequest request) {
    return request
        .bodyToMono(UpdateCourseRequest.class)
        .onErrorResume(Mono::error)
        .map(r -> courseService.updateCourse(r))
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> assignStudentToCourse(ServerRequest request) {
    UUID courseId = UUID.fromString(request.pathVariable("courseId"));
    UUID studentId = UUID.fromString(request.pathVariable("studentId"));

    return courseService
        .assignUserToCourse(studentId, courseId)
        .onErrorResume(Mono::error)
        .flatMap(r -> ServerResponse.ok().build());
  }
}
