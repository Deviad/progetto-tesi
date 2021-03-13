package io.deviad.ripeti.webapp.application.command;

import io.deviad.ripeti.webapp.domain.entity.AnswerEntity;
import io.deviad.ripeti.webapp.domain.entity.QuestionEntity;
import io.deviad.ripeti.webapp.domain.entity.QuizEntity;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.persistence.repository.AnswerRepository;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuestionRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuizRepository;
import io.deviad.ripeti.webapp.ui.command.create.AddQuizToCourseCommand;
import io.deviad.ripeti.webapp.ui.command.create.CreateAnswerDto;
import io.deviad.ripeti.webapp.ui.command.create.CreateQuestionCommand;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class QuizCommandService {

  CourseRepository courseRepository;
  QuizRepository quizRepository;
  Common common;
  QuestionRepository questionRepository;
  AnswerRepository answerRepository;

  @Transactional
  public Mono<Void> addOrUpdateQuiz(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @RequestBody(required = true) AddQuizToCourseCommand quiz,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final OAuth2IntrospectionAuthenticatedPrincipal principal = common.getPrincipalFromToken(token);
    if (!isTeacher(principal)) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only teachers can do this"));
    }

    return Flux.fromIterable(quiz.quizzes())
        .flatMap(x -> Mono.zip(Mono.just(x.questions()), Mono.just(x)))
        .flatMap(
            t ->
                saveQuestions(t.getT1())
                    .flatMap(questionEntities -> saveAnswers(t.getT1(), questionEntities))
                    .flatMap(
                        tuple ->
                            Mono.zip(
                                Mono.just(updateQuestionsWithAnswers(tuple)),
                                Mono.just(t.getT2()))))
        .flatMap(t1 -> Mono.zip(saveUpdatedQuestions(t1.getT1()), Mono.just(t1.getT2())))
        .flatMap(t2 -> Mono.just(saveQuiz(t2.getT2(), quizRepository).apply(t2.getT1())))
        .flatMap(
            qe ->
                Mono.zip(
                    qe,
                    courseRepository
                        .findById(courseId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")))
                        .onErrorResume(Mono::error)))
        .flatMap(
            t3 ->
                courseRepository
                    .save(t3.getT2().addQuizToCourse(t3.getT1().id()))
                    .onErrorResume(Mono::error))
        .then(Mono.empty());
  }

  private Mono<Set<UUID>> saveUpdatedQuestions(Set<QuestionEntity> set) {
    return questionRepository
        .saveAll(set)
        .onErrorResume(Flux::error)
        .map(QuestionEntity::id)
        .collect(Collectors.toSet());
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

  public boolean isTeacher(OAuth2IntrospectionAuthenticatedPrincipal principal) {
    return ((List)
            ((Map) ((Map) principal.getClaims().get("resource_access")).get("ripeti-web"))
                .get("roles"))
        .contains(Role.PROFESSOR.name());
  }

  private static Set<QuestionEntity> updateQuestionsWithAnswers(
      Tuple2<Set<QuestionEntity>, Set<UUID>> tuple) {
    return tuple.getT1().stream()
        .map(
            x ->
                x.withAnswerIds(
                    Stream.of(x.answerIds(), tuple.getT2())
                        .flatMap(Stream::ofNullable)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toUnmodifiableSet())))
        .collect(Collectors.toSet());
  }

  private Mono<Tuple2<Set<QuestionEntity>, Set<UUID>>> saveAnswers(
      Set<CreateQuestionCommand> questions, Set<QuestionEntity> questionEntities) {
    return Mono.from(
        Flux.fromIterable(questions)
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

  private Mono<Set<QuestionEntity>> saveQuestions(Set<CreateQuestionCommand> questions) {

    final var qd =
        questions.stream()
            .map(x -> QuestionEntity.builder().id(x.getId()).title(x.getTitle()).build())
            .collect(Collectors.toSet());

    return questionRepository.saveAll(qd).collect(Collectors.toSet());
  }

  private static Function<Set<UUID>, Mono<QuizEntity>> saveQuiz(
      AddQuizToCourseCommand.Quiz quizDetails, QuizRepository quizRepository) {
    return qs ->
        quizRepository
            .save(
                QuizEntity.builder()
                    .id(quizDetails.id())
                    .questionIds(qs)
                    .quizName(quizDetails.quizName())
                    .quizContent(quizDetails.quizContent())
                    .build())
            .onErrorResume(Mono::error);
  }

  private Set<AnswerEntity> mapToAnswerEntity(Set<CreateAnswerDto> answers) {
    return answers.stream()
        .map(
            adto ->
                AnswerEntity.builder()
                    .id(adto.id())
                    .value(adto.value())
                    .title(adto.title())
                    .build())
        .collect(Collectors.toSet());
  }
}
