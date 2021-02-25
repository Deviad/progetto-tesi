package io.deviad.ripeti.webapp.application.command;

import io.deviad.ripeti.webapp.Utils;
import io.deviad.ripeti.webapp.adapter.MappingUtils;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.entity.AnswerEntity;
import io.deviad.ripeti.webapp.domain.entity.LessonEntity;
import io.deviad.ripeti.webapp.domain.entity.QuestionEntity;
import io.deviad.ripeti.webapp.domain.entity.QuizEntity;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.persistence.repository.AnswerRepository;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.LessonRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuestionRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuizRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import io.deviad.ripeti.webapp.ui.command.AddLessonToCourseRequest;
import io.deviad.ripeti.webapp.ui.command.AddQuizToCourseRequest;
import io.deviad.ripeti.webapp.ui.command.AnswerDto;
import io.deviad.ripeti.webapp.ui.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.ui.command.UpdateCourseRequest;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class CourseCommandService {

  CourseRepository courseRepository;
  UserRepository userRepository;
  LessonRepository lessonRepository;
  QuizRepository quizRepository;
  QuestionRepository questionRepository;
  AnswerRepository answerRepository;
  Validator validator;
  Common common;

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

    return verifyCourseOwner(courseId, email)
        .flatMap(
            x ->
                courseRepository
                    .save(
                        x.getT1()
                            .updateCourseInformation(
                                request.courseName(), request.courseDescription()))
                    .onErrorResume(Mono::error));
  }

  @Transactional
  public Mono<Void> deleteCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {
    String email = common.getEmailFromToken(token);
    return verifyCourseOwner(courseId, email)
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

    return verifyCourseOwner(courseId, email)
        .flatMap(t -> courseRepository.save(t.getT1().publishCourse()));
  }

  @Transactional
  public Mono<CourseAggregate> addLessonToCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @RequestBody(required = true) AddLessonToCourseRequest lessonDetails,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    LessonEntity lessonEntity = MappingUtils.MAPPER.convertValue(lessonDetails, LessonEntity.class);

    final String email = common.getEmailFromToken(token);

    Mono<LessonEntity> lesson = lessonRepository.save(lessonEntity).onErrorResume(Mono::error);

    return verifyCourseOwner(courseId, email)
        .flatMap(t -> Mono.zip(Mono.just(t.getT1()), lesson))
        .flatMap(
            t -> {
              t.getT1().addLessonToCourse(t.getT2().id());
              return courseRepository.save(t.getT1());
            })
        .flatMap(Mono::just);
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

  @Transactional
  public Mono<Void> addQuizToCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @RequestBody(required = true) AddQuizToCourseRequest quizDetails,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final OAuth2IntrospectionAuthenticatedPrincipal principal = common.getPrincipalFromToken(token);
    if (!isTeacher(principal)) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only teachers can do this"));
    }

    return Mono.from(
            createInitialQuestions(quizDetails)
                .flatMap(questionEntities -> saveAnswers(quizDetails, questionEntities))
                .flatMap(tuple -> Mono.from(updateQuestionsWithAnswers(tuple))))
        .flatMap(saveUpdatedQuestions(questionRepository))
        .flatMap(saveQuiz(quizDetails, quizRepository))
        .flatMap(
            qiz ->
                Mono.zip(
                    Mono.just(qiz),
                    courseRepository
                        .findById(courseId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")))
                        .onErrorResume(Mono::error)))
        .flatMap(
            t ->
                courseRepository
                    .save(t.getT2().addQuizToCourse(t.getT1().id()))
                    .onErrorResume(Mono::error))
        .then(Mono.empty());
  }

  @Transactional
  public Mono<Void> removeQuizFromCourse(
      @Parameter(in = ParameterIn.PATH) UUID quizId,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final OAuth2IntrospectionAuthenticatedPrincipal principal = common.getPrincipalFromToken(token);
    if (!isTeacher(principal)) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only teachers can do this"));
    }

    Mono<QuizEntity> quiz =
        quizRepository
            .findById(quizId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Quiz does not exist")));

    return quiz.then(quizRepository.deleteById(quizId))
        .onErrorResume(Mono::error)
        .then(Mono.empty());
  }

  private static Function<Set<UUID>, Mono<QuizEntity>> saveQuiz(
      AddQuizToCourseRequest quizDetails, QuizRepository quizRepository) {
    return qs ->
        quizRepository
            .save(
                QuizEntity.builder()
                    .questionIds(qs)
                    .quizName(quizDetails.quizName())
                    .quizContent(quizDetails.quizContent())
                    .build())
            .onErrorResume(Mono::error);
  }

  private static Function<Set<QuestionEntity>, Mono<Set<UUID>>> saveUpdatedQuestions(
      QuestionRepository questionRepository) {
    return questions ->
        questionRepository
            .saveAll(questions)
            .onErrorResume(Flux::error)
            .map(QuestionEntity::id)
            .collect(Collectors.toSet());
  }

  private static Flux<Set<QuestionEntity>> updateQuestionsWithAnswers(
      Tuple2<Set<QuestionEntity>, Set<UUID>> tuple) {
    return Flux.just(
        tuple.getT1().stream()
            .map(
                x ->
                    x.withAnswerIds(
                        Stream.of(x.answerIds(), tuple.getT2())
                            .flatMap(Stream::ofNullable)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toUnmodifiableSet())))
            .collect(Collectors.toSet()));
  }

  private Mono<Tuple2<Set<QuestionEntity>, Set<UUID>>> saveAnswers(
      AddQuizToCourseRequest quizDetails, Set<QuestionEntity> questionEntities) {
    return Mono.from(
        Flux.fromIterable(quizDetails.questions())
            .flatMap(
                x -> {
                  var answers = mapToAnswerEntity(x.getAnswers());
                  Mono<Set<UUID>> answerIds =
                      answerRepository
                          .saveAll(answers)
                          .onErrorResume(Flux::error)
                          .map(AnswerEntity::id)
                          .collect(Collectors.toSet());
                  return Mono.zip(Mono.just(questionEntities), answerIds);
                }));
  }

  private Mono<Set<QuestionEntity>> createInitialQuestions(AddQuizToCourseRequest quizDetails) {

    final var qd =
        quizDetails.questions().stream()
            .map(x -> QuestionEntity.builder().title(x.getTitle()).build())
            .collect(Collectors.toSet());

    return questionRepository.saveAll(qd).collect(Collectors.toSet());
  }

  private Set<AnswerEntity> mapToAnswerEntity(Set<AnswerDto> answers) {
    return answers.stream()
        .map(adto -> AnswerEntity.builder().correct(adto.correct()).title(adto.title()).build())
        .collect(Collectors.toSet());
  }

  private Mono<CourseAggregate> getCourseByCourseId(UUID courseId) {
    return courseRepository
        .findById(courseId)
        .onErrorResume(Mono::error)
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course does not exist")));
  }

  private Mono<Tuple2<CourseAggregate, UserAggregate>> verifyCourseOwner(
      @Parameter(in = ParameterIn.PATH) UUID courseId, String email) {
    return isTeacherOfCourse(courseId, email)
        .filter(tuple -> tuple.getT1().teacherId().equals(tuple.getT2().id()))
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't own this course")))
        .onErrorResume(Mono::error);
  }

  private Mono<Tuple2<CourseAggregate, UserAggregate>> isTeacherOfCourse(
      UUID courseId, String email) {
    return getCourseByCourseId(courseId)
        .flatMap(c -> Mono.zip(Mono.just(c), common.getUserByEmail(email)))
        .filter(tuple -> tuple.getT2().role().equals(Role.PROFESSOR))
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a teacher")))
        .onErrorResume(Mono::error);
  }

  boolean isTeacher(OAuth2IntrospectionAuthenticatedPrincipal principal) {
    return ((List)
            ((Map) ((Map) principal.getClaims().get("resource_access")).get("ripeti-web"))
                .get("roles"))
        .contains(Role.PROFESSOR.name());
  }
}
