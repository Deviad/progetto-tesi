package io.deviad.ripeti.webapp.api.route;

import io.deviad.ripeti.webapp.api.command.RegistrationRequest;
import io.deviad.ripeti.webapp.api.command.UpdateRequest;
import io.deviad.ripeti.webapp.application.UserCommandService;
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
public class UserCommandRoutesManager {

  UserCommandService userManagement;

  @Bean
  public RouterFunction<ServerResponse> userCommandRoutes() {
    return route()
        .POST(
            "/api/user",
            RequestPredicates.contentType(MediaType.APPLICATION_JSON),
            this::handleRegistration)
        .PUT(
            "/api/user",
            RequestPredicates.contentType(MediaType.APPLICATION_JSON),
            this::handleUpdate)
        .build();
  }

  Mono<ServerResponse> handleRegistration(ServerRequest request) {
    return request
        .bodyToMono(RegistrationRequest.class)
        .onErrorResume(Mono::error)
        .map(r -> userManagement.registerUser(r))
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }

  Mono<ServerResponse> handleUpdate(ServerRequest request) {
    return request
        .bodyToMono(UpdateRequest.class)
        .onErrorResume(Mono::error)
        .map(r -> userManagement.updateUser(r))
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }
}
