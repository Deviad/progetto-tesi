package io.deviad.ripeti.webapp.application.command;

import io.deviad.ripeti.webapp.adapter.MappingUtils;
import io.deviad.ripeti.webapp.api.command.AddLessonToCourseRequest;
import io.deviad.ripeti.webapp.api.command.AddQuizToCourseRequest;
import io.deviad.ripeti.webapp.api.command.AnswerDto;
import io.deviad.ripeti.webapp.api.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.api.command.UpdateCourseRequest;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.entity.AnswerEntity;
import io.deviad.ripeti.webapp.domain.entity.LessonEntity;
import io.deviad.ripeti.webapp.domain.entity.QuestionEntity;
import io.deviad.ripeti.webapp.domain.entity.QuizEntity;
import io.deviad.ripeti.webapp.persistence.repository.AnswerRepository;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.LessonRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuestionRepository;
import io.deviad.ripeti.webapp.persistence.repository.QuizRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CourseCommandService {

  CourseRepository courseRepository;
  UserRepository userRepository;
  LessonRepository lessonRepository;
  QuizRepository quizRepository;
  QuestionRepository questionRepository;
  AnswerRepository answerRepository;

  @Transactional
  public Mono<CourseAggregate> createCourse(CreateCourseRequest request) {
    return courseRepository.save(
        CourseAggregate.createCourse(
            request.courseName(), request.courseDescription(), request.teacherId()));
  }

  @Transactional
  public Mono<CourseAggregate> updateCourse(UpdateCourseRequest request) {
    return courseRepository
        .findById(request.courseId())
        .onErrorResume(Mono::error)
        .flatMap(
            x ->
                courseRepository.save(
                    x.updateCourseInformation(request.courseName(), request.courseDescription())));
  }

  @Transactional
  public Mono<Optional<CourseAggregate>> getCourseById(UUID uuid) {
    return courseRepository
        .findById(uuid)
        .onErrorResume(Mono::error)
        .flatMap(x -> Mono.just(Optional.of(x)))
        .defaultIfEmpty(Optional.empty());
  }

  @Transactional
  public Mono<Void> deleteCourse(UUID courseId) {
    return courseRepository
        .deleteById(courseId)
        .onErrorResume(Mono::error)
        .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")))
        .flatMap(x -> Mono.empty());
  }

  @Transactional
  public Mono<Void> assignUserToCourse(UUID userId, UUID courseId) {
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
        .flatMap(t -> courseRepository.save(t.getT1().assignStudentToCourse(t.getT2().id())))
        .flatMap(c -> Mono.empty());
  }

  @Transactional
  public Mono<Void> unassignUserFromCourse(UUID userId, UUID courseId) {
    Mono<CourseAggregate> course =
        courseRepository
            .findById(courseId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")));
    Mono<UserAggregate> user =
        userRepository
            .findById(courseId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")));

    return course
        .flatMap(c -> Mono.zip(Mono.just(c), user))
        .map(t -> t.getT1().removeStudentFromCourse(t.getT2().id()))
        .flatMap(c -> Mono.empty());
  }

  @Transactional
  public Mono<CourseAggregate> publishCourse(UUID courseId, UUID teacherId) {

    Mono<CourseAggregate> course =
        courseRepository
            .findById(courseId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")));

    Mono<UserAggregate> user =
        userRepository
            .findById(teacherId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Teacher does not exist")));

    return user.flatMap(x -> Mono.zip(Mono.just(x), course))
        .flatMap(
            x -> {
              if (!x.getT2().teacherId().equals(x.getT1().id())) {
                return Mono.error(
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "You do not own this course"));
              }
              return course;
            })
        .onErrorResume(Mono::error)
        .flatMap(x -> courseRepository.save(x.publishCourse()));
  }

  @Transactional
  public Mono<Void> addLessonToCourse(UUID courseId, AddLessonToCourseRequest lessonDetails) {

    LessonEntity lessonEntity = MappingUtils.MAPPER.convertValue(lessonDetails, LessonEntity.class);

    Mono<CourseAggregate> course =
        courseRepository
            .findById(courseId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")));
    Mono<LessonEntity> lesson = lessonRepository.save(lessonEntity).onErrorResume(Mono::error);

    return course
        .flatMap(c -> Mono.zip(Mono.just(c), lesson))
        .flatMap(
            t -> {
              t.getT1().addLessonToCourse(t.getT2().id());
              return courseRepository.save(t.getT1());
            })
        .flatMap(c -> Mono.empty());
  }

  @Transactional
  public Mono<Void> removeLessonFromCourse(UUID lessonId) {

    Mono<LessonEntity> lesson =
        lessonRepository
            .findById(lessonId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Lesson does not exist")));

    return lesson.flatMap(c -> lessonRepository.deleteById(lessonId)).flatMap(c -> Mono.empty());
  }

  @Transactional
  public Mono<Void> addQuizToCourse(UUID courseId, AddQuizToCourseRequest quizDetails) {

    return Mono.from(
            createInitialQuestions(quizDetails)
                .flatMap(questionEntities -> saveAnswers(quizDetails, questionEntities))
                .flatMap(tuple -> Mono.from(updateQuestionsWithAnswers(tuple))))
        .flatMap(saveUpdatedQuestions(questionRepository))
        .flatMap(getSetMonoFunction(quizDetails, quizRepository))
        .flatMap(qiz -> Mono.zip(Mono.just(qiz), courseRepository.findById(courseId)))
        .flatMap(t -> courseRepository.save(t.getT2().addQuizToCourse(t.getT1().id())))
        .flatMap(c -> Mono.empty());
  }

  private static Function<Set<UUID>, Mono<? extends QuizEntity>> getSetMonoFunction(
      AddQuizToCourseRequest quizDetails, QuizRepository quizRepository) {
    return qs ->
        quizRepository.save(
            QuizEntity.builder()
                .questionIds(qs)
                .quizName(quizDetails.quizName())
                .quizContent(quizDetails.quizContent())
                .build());
  }

  private static Function<Set<QuestionEntity>, Mono<? extends Set<UUID>>> saveUpdatedQuestions(
      QuestionRepository questionRepository) {
    return questions ->
        questionRepository.saveAll(questions).map(QuestionEntity::id).collect(Collectors.toSet());
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
        .map(
            adto ->
                AnswerEntity.builder().correct(adto.getCorrect()).title(adto.getTitle()).build())
        .collect(Collectors.toSet());
  }

  @Transactional
  public Mono<Void> removeQuizFromCourse(UUID quizId) {

    Mono<QuizEntity> quiz =
        quizRepository
            .findById(quizId)
            .onErrorResume(Mono::error)
            .switchIfEmpty(Mono.error(new RuntimeException("Quiz does not exist")));

    return quiz.flatMap(c -> quizRepository.deleteById(quizId)).flatMap(c -> Mono.empty());
  }
}
