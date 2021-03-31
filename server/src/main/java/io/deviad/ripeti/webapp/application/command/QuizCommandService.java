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
import lombok.SneakyThrows;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
  @SneakyThrows
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

            .flatMap(q-> Flux.fromIterable(q.questions())
                            .flatMap(qs-> answerRepository
                                    .saveAll(mapToAnswerEntity(qs.getAnswers()))
                                            .collect(Collectors.toSet())
                                            .flatMap(l-> Mono.just(l.stream().map(AnswerEntity::id).collect(Collectors.toSet())))
                                            .flatMap(l -> Mono.defer(()-> Mono.just(toQuestionEntity(qs,l)))))
                                    .collect(Collectors.toSet())
                                            .flatMap(l-> questionRepository.saveAll(l).collect(Collectors.toSet()))
                                            .map(l->l.stream().map(QuestionEntity::id).collect(Collectors.toSet()))
                                            .flatMapMany(qsl-> quizRepository.save(toQuizEntity(q, qsl)))
                                            .doOnNext(System.out::println))
            .delayUntil(qe-> courseRepository.findById(courseId).map(x->x.addQuizToCourse(qe.id())).flatMap(c->courseRepository.save(c)))
            .doOnNext(System.out::println)
            .then(Mono.empty());

  }

  private QuizEntity toQuizEntity(AddQuizToCourseCommand.Quiz q, Set<UUID> qsIds) {
    return QuizEntity.builder()
            .id(q.id())
            .questionIds(qsIds)
            .quizName(q.quizName())
            .quizContent(q.quizContent())
            .build();
  }

  private QuestionEntity toQuestionEntity( CreateQuestionCommand qs, Set<UUID> ansIds) {
    return QuestionEntity.builder()
            .id(qs.getId())
            .answerIds(ansIds)
            .title(qs.getTitle()).build();
  }

  @Transactional
  public Mono<Void> removeQuizzesFromCourse(
      @Parameter(in = ParameterIn.PATH) Set<UUID> quizIds,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final OAuth2IntrospectionAuthenticatedPrincipal principal = common.getPrincipalFromToken(token);
    if (!isTeacher(principal)) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only teachers can do this"));
    }

    return Flux.fromIterable(quizIds)
            .flatMap(qId->  quizRepository.findById(qId))
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Quiz does not exist")))
            .flatMap(x->quizRepository.deleteById(x.id()))
            .onErrorResume(Mono::error)
            .then(Mono.empty());

  }

  public boolean isTeacher(OAuth2IntrospectionAuthenticatedPrincipal principal) {
    return ((List)
            ((Map) ((Map) principal.getClaims().get("resource_access")).get("ripeti-web"))
                .get("roles"))
        .contains(Role.PROFESSOR.name());
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
