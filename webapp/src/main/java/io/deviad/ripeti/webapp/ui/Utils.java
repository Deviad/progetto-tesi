package io.deviad.ripeti.webapp.ui;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.Principal;

@UtilityClass
public class Utils {

    public static Mono<? extends Principal> fetchPrincipal(ServerRequest request) {
      return request
              .principal()
              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No token provided")))
              .onErrorResume(Mono::error);
    }
}
