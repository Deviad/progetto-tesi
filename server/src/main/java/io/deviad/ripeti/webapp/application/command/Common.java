package io.deviad.ripeti.webapp.application.command;

import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.UUID;

@Service
@AllArgsConstructor
public class Common {
  private final UserRepository repository;
  private final CourseRepository courseRepository;
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

    public Mono<Tuple2<CourseAggregate, UserAggregate>> verifyCourseOwner(
            @Parameter(in = ParameterIn.PATH) UUID courseId, String email) {
      return isTeacherOfCourse(courseId, email)
          .filter(tuple -> tuple.getT1().teacherId().equals(tuple.getT2().id()))
          .switchIfEmpty(
              Mono.error(
                  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't own this course")))
          .onErrorResume(Mono::error);
    }

  Mono<CourseAggregate> getCourseByCourseId(UUID courseId) {
    return courseRepository
        .findById(courseId)
        .onErrorResume(Mono::error)
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course does not exist")));
  }

  public Mono<Tuple2<CourseAggregate, UserAggregate>> isTeacherOfCourse(
          UUID courseId, String email) {
    return getCourseByCourseId(courseId)
        .flatMap(c -> Mono.zip(Mono.just(c), getUserByEmail(email)))
        .filter(tuple -> tuple.getT2().role().equals(Role.PROFESSOR))
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a teacher")))
        .onErrorResume(Mono::error);
  }
}
