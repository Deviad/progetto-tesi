package io.deviad.ripeti.webapp.application.command;

import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class Common {
  private final UserRepository repository;

  Mono<UserAggregate> getUserByEmail(String email) {
    return repository
        .getUserAggregateByEmail(email)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot find user")))
        .onErrorResume(Mono::error);
  }

  String getEmailFromToken(JwtAuthenticationToken token) {
    var principal =
        new OAuth2IntrospectionAuthenticatedPrincipal(
            token.getTokenAttributes(), token.getAuthorities());

    return principal.getAttribute("email");
  }

  OAuth2IntrospectionAuthenticatedPrincipal getPrincipalFromToken(JwtAuthenticationToken token) {
    return new OAuth2IntrospectionAuthenticatedPrincipal(
        token.getTokenAttributes(), token.getAuthorities());
  }
}
