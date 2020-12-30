package io.deviad.ripeti.webapp.api.route;

import io.deviad.ripeti.webapp.api.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.api.command.UpdateCourseRequest;
import io.deviad.ripeti.webapp.api.command.UpdateRequest;
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
        .build();
  }

  Mono<ServerResponse> createCourse(ServerRequest request) {
    return request
        .bodyToMono(CreateCourseRequest.class)
        .onErrorResume(Mono::error)
        .map(r -> courseService.createCourse(r))
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> handleUpdate(ServerRequest request) {
    return request
        .bodyToMono(UpdateCourseRequest.class)
        .onErrorResume(Mono::error)
        .map(r -> courseService.updateCourse(r))
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }
}
