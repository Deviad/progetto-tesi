package io.deviad.ripeti.webapp.api.route;

import io.deviad.ripeti.webapp.api.queries.CourseInfo;
import io.deviad.ripeti.webapp.api.queries.UserInfoDto;
import io.deviad.ripeti.webapp.application.CourseQueryService;
import io.deviad.ripeti.webapp.application.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

  @Bean
  public RouterFunction<ServerResponse> courseQueryRoutes() {
    return route()
            .GET("/api/course/{courseId}", this::handleGetCourse)
            .GET("/api/course/{courseId}/getstudents", this::handleGetEnrolledStudents)
            .GET("/api/course/teacher/{teacherId}", this::handleGetByTeacherId)
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
            .map(r -> queryService.getCourseByTeacherId(r))
            .switchIfEmpty(Mono.error(new RuntimeException("Cannot find students")))
            .flatMap(r -> ServerResponse.ok().body(r, CourseInfo.class));
  }
}
