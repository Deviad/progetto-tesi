package io.deviad.ripeti.webapp.application.command;

import io.deviad.ripeti.webapp.Utils;
import io.deviad.ripeti.webapp.adapter.MappingUtils;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.entity.LessonEntity;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.persistence.repository.AnswerRepository;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.LessonRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuestionRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import io.deviad.ripeti.webapp.ui.command.LessonCommand;
import io.deviad.ripeti.webapp.ui.command.create.AddQuizToCourseCommand;
import io.deviad.ripeti.webapp.ui.command.create.CreateCourseRequest;
import io.deviad.ripeti.webapp.ui.command.update.UpdateCourseRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class CourseCommandService {

  CourseRepository courseRepository;
  UserRepository userRepository;
  LessonRepository lessonRepository;
  QuizCommandService quizCommandService;
  AnswerRepository answerRepository;
  Validator validator;
  Common common;
  LessonCommandService lessonCommandService;

  @Transactional
  public Mono<CourseAggregate> createCourse(
      @RequestBody(required = true) CreateCourseRequest request,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    Utils.handleValidation(MappingUtils.MAPPER, validator, request);

    String email = common.getEmailFromToken(token);

    final Function<UUID, Mono<CourseAggregate>> saveCourseAggregate =
        id ->
            courseRepository.save(
                CourseAggregate.createCourse(
                    request.courseName(), request.courseDescription(), id));

    return common
        .getUserByEmail(email)
        .filter(t -> t.role().equals(Role.PROFESSOR))
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a teacher")))
        .onErrorResume(Mono::error)
        .flatMap(t -> saveCourseAggregate.apply(t.id()).onErrorResume(Mono::error))
        .flatMap(Mono::just);
  }

  @Transactional
  public Mono<CourseAggregate> updateCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @RequestBody(required = true) UpdateCourseRequest request,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    Utils.handleValidation(MappingUtils.MAPPER, validator, request);

    String email = common.getEmailFromToken(token);

    return common
        .verifyCourseOwner(courseId, email)
        .flatMap(
            x ->
                courseRepository
                    .save(
                        x.getT1()
                            .updateCourseInformation(
                                request.getCourseName(), request.getCourseDescription()))
                    .onErrorResume(Mono::error));
  }

  @Transactional
  public Mono<Void> deleteCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {
    String email = common.getEmailFromToken(token);
    return common
        .verifyCourseOwner(courseId, email)
        .flatMap(x -> courseRepository.deleteById(courseId).onErrorResume(Mono::error))
        .flatMap(x -> Mono.empty());
  }

  @Transactional
  public Mono<Void> assignStudentToCourse(
      @Parameter(in = ParameterIn.PATH) UUID userId, UUID courseId) {
    Mono<CourseAggregate> course =
        courseRepository
            .findById(courseId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")));
    Mono<UserAggregate> user =
        userRepository
            .findById(userId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")));

    return course
        .flatMap(c -> Mono.zip(Mono.just(c), user))
        .flatMap(
            t ->
                courseRepository
                    .save(t.getT1().assignStudentToCourse(t.getT2().id()))
                    .onErrorResume(Mono::error))
        .flatMap(c -> Mono.empty());
  }

  @Transactional
  public Mono<Void> unassignUserFromCourse(
      @Parameter(in = ParameterIn.PATH) UUID userId,
      @Parameter(in = ParameterIn.PATH) UUID courseId) {
    Mono<CourseAggregate> course =
        courseRepository
            .findById(courseId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")));
    Mono<UserAggregate> user =
        userRepository
            .findById(userId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")));

    return course
        .flatMap(c -> Mono.zip(Mono.just(c), user))
        .map(t -> courseRepository.save(t.getT1().removeStudentFromCourse(t.getT2().id())))
        .flatMap(c -> Mono.empty());
  }

  @Transactional
  public Mono<CourseAggregate> publishCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final String email = common.getEmailFromToken(token);

    return common
        .verifyCourseOwner(courseId, email)
        .flatMap(t -> courseRepository.save(t.getT1().publishCourse()));
  }

  @Transactional
  public Mono<Void> removeLessonFromCourse(
      @Parameter(in = ParameterIn.PATH) UUID lessonId,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final OAuth2IntrospectionAuthenticatedPrincipal principal = common.getPrincipalFromToken(token);
    if (!isTeacher(principal)) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only teachers can do this"));
    }

    Mono<LessonEntity> lesson =
        lessonRepository
            .findById(lessonId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Lesson does not exist")));

    return lesson.flatMap(c -> lessonRepository.deleteById(lessonId)).flatMap(c -> Mono.empty());
  }

  public Mono<ServerResponse> addUpdateLessonHandler(
          ServerRequest request, Class<? extends LessonCommand<?>> command) {
    return lessonCommandService.addUpdateLessonHandler(request, command);
  }

  public boolean isTeacher(OAuth2IntrospectionAuthenticatedPrincipal principal) {
    return ((List)
            ((Map) ((Map) principal.getClaims().get("resource_access")).get("ripeti-web"))
                .get("roles"))
        .contains(Role.PROFESSOR.name());
  }

  public Mono<Void> addQuizToCourse( UUID courseId,
                                     AddQuizToCourseCommand quizDetails,
                                     JwtAuthenticationToken token) {
    return quizCommandService.addQuizToCourse(courseId, quizDetails, token);
  }

  public Mono<Void> removeQuizFromCourse(
          UUID quizId,
          JwtAuthenticationToken token) {
    return quizCommandService.removeQuizFromCourse(quizId, token);

  }
}
