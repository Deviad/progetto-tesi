package io.deviad.ripeti.webapp.ui.route;

import io.deviad.ripeti.webapp.ui.command.RegistrationRequest;
import io.deviad.ripeti.webapp.ui.command.UpdatePasswordRequest;
import io.deviad.ripeti.webapp.ui.command.UpdateUserRequest;
import io.deviad.ripeti.webapp.application.command.UserCommandService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
        .PUT(
            "/api/user/changePassword",
            RequestPredicates.contentType(MediaType.APPLICATION_JSON),
            this::handleUpdatePassword)
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


  Mono<ServerResponse> handleUpdatePassword(ServerRequest request) {

    return request
            .bodyToMono(UpdatePasswordRequest.class)
            .onErrorResume(Mono::error)
            .flatMap(r-> Mono.zip(Mono.just(r), request.principal().onErrorResume(Mono::error)))
            .map(r -> userManagement.updatePassword(r.getT1(), (JwtAuthenticationToken)r.getT2()))
            .flatMap(Function.identity())
            .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }


  Mono<ServerResponse> handleUpdate(ServerRequest request) {

    return request
        .bodyToMono(UpdateUserRequest.class)
        .onErrorResume(Mono::error)
        .flatMap(r-> Mono.zip(Mono.just(r), request.principal().onErrorResume(Mono::error)))
        .map(r -> userManagement.updateUser(r.getT1(), (JwtAuthenticationToken)r.getT2()))
        .flatMap(Function.identity())
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }
}
