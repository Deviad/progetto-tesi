package io.deviad.ripeti.webapp.application.command;

import com.fasterxml.jackson.core.type.TypeReference;
import io.deviad.ripeti.webapp.adapter.MappingUtils;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.entity.LessonEntity;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.LessonRepository;
import io.deviad.ripeti.webapp.ui.Utils;
import io.deviad.ripeti.webapp.ui.command.LessonCommand;
import io.deviad.ripeti.webapp.ui.command.create.AddLessonsToCourseRequestCommand;
import io.deviad.ripeti.webapp.ui.command.update.UpdateLessonsCommand;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.vavr.Tuple;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class LessonCommandService {

  Common common;
  CourseRepository courseRepository;
  LessonRepository lessonRepository;

  public Mono<ServerResponse> addUpdateLessonHandler(
      ServerRequest request, Class<? extends LessonCommand<?>> command) {
    UUID courseId = UUID.fromString(request.pathVariable("courseId"));

    return Utils.fetchPrincipal(request)
        .flatMap(
            p ->
                Mono.zip(
                    Mono.just(p),
                    request
                        .bodyToMono(command)
                        .map(LessonCommand::getLessons)
                        .onErrorResume(Mono::error)))
        .flatMap(
            t -> {
              if (command.equals(AddLessonsToCourseRequestCommand.class)) {
                return addLessons(courseId, t);
              } else if (command.equals(UpdateLessonsCommand.class)) {
                return updateLessons(courseId, t);
              }
              return Mono.error(
                  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad command provided"));
            })
        .flatMap(x -> ServerResponse.ok().bodyValue(x));
  }

  @Transactional
  public Mono<List<CourseAggregate>> addLessonsToCourse(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @RequestBody(required = true) List<AddLessonsToCourseRequestCommand.Lesson> lessonDetails,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    List<LessonEntity> lessonEntities =
        MappingUtils.MAPPER.convertValue(lessonDetails, new TypeReference<>() {});

    final String email = common.getEmailFromToken(token);

    Flux<LessonEntity> lessons =
        lessonRepository.saveAll(lessonEntities).onErrorResume(Mono::error);

    return Flux.from(common.verifyCourseOwner(courseId, email))
        .join(lessons, s -> Flux.never(), s -> Flux.never(), Tuple::of)
        .flatMap(
            t -> {
              t._1().getT1().addLessonToCourse(t._2.id());
              return courseRepository.save(t._1().getT1());
            })
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Transactional
  public Mono<List<LessonEntity>> updateLessons(
      @Parameter(in = ParameterIn.PATH) UUID courseId,
      @RequestBody(required = true) List<UpdateLessonsCommand.Lesson> lessonDetails,
      @Parameter(required = true, in = ParameterIn.HEADER) JwtAuthenticationToken token) {

    final String email = common.getEmailFromToken(token);

    var lessons =
        lessonRepository
            .findAllById(lessonDetails.stream().map(UpdateLessonsCommand.Lesson::getId).collect(Collectors.toList()))
            .onErrorResume(Mono::error);

    return Flux.from(common.verifyCourseOwner(courseId, email))
        .onErrorResume(Mono::error)
        .thenMany(Flux.defer(() -> Flux.zip(lessons, Flux.fromIterable(lessonDetails).onErrorResume(Flux::error), (l, ld)-> {
            l.lessonName(ld.getLessonName());
            l.lessonContent(ld.getLessonContent());
            return l;
        })))
        .collect(Collectors.toList())
        .doOnNext(System.out::println)
        .flatMapMany(ls -> lessonRepository.saveAll(ls).onErrorResume(Flux::error))
        .collect(Collectors.toList());
  }

  private Mono<List<LessonEntity>> updateLessons(
      UUID courseId, Tuple2<? extends Principal, ? extends List<?>> t) {
    return updateLessons(courseId, (List<UpdateLessonsCommand.Lesson>) t.getT2(), (JwtAuthenticationToken) t.getT1());
  }

  private Mono<List<CourseAggregate>> addLessons(
      UUID courseId, Tuple2<? extends Principal, ? extends List<?>> t) {
    return addLessonsToCourse(
        courseId,
        (List<AddLessonsToCourseRequestCommand.Lesson>) t.getT2(),
        (JwtAuthenticationToken) t.getT1());
  }
}
