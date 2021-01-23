package io.deviad.ripeti.webapp.api.route;

import io.deviad.ripeti.webapp.application.query.UserQueryService;
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
public class UserQueryRoutesManager {

  UserQueryService queryService;

  @Bean
  public RouterFunction<ServerResponse> userQueryRoutes() {
    return route().GET("/api/user/{username}", this::handleGetUserinfo).build();
  }

  Mono<ServerResponse> handleGetUserinfo(ServerRequest request) {
    return Mono.just(request.pathVariable("username"))
        .onErrorResume(Mono::error)
        .flatMap(r -> queryService.getUserInfo(r))
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot find user")))
        .flatMap(r -> ServerResponse.ok().bodyValue(r));
  }
}
