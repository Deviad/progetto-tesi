package io.deviad.ripeti.webapp.application;

import io.deviad.ripeti.webapp.api.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.api.command.UpdateCourseRequest;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseCommandService {

  CourseRepository courseRepository;
  UserRepository userRepository;

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
        .flatMap(x->courseRepository.save(x.publishCourse()));
  }

  //    @Transactional
  //    public Mono<Void> assignLessonToCourse(UUID lessonId, UUID courseId) {
  //        Mono<CourseAggregate> course = courseRepository.findById(courseId)
  //                .onErrorResume(Mono::error)
  //                .switchIfEmpty(Mono.error(new RuntimeException("Course does not exist")));
  //        Mono<UserAggregate> lesson = userRepository.findById(lessonId)
  //                .onErrorResume(Mono::error)
  //                .switchIfEmpty(Mono.error(new RuntimeException("User does not exist")));
  //
  //        return course
  //                .flatMap(c->Mono.zip(Mono.just(c), lesson))
  //                .map(t -> t.getT1().addLessonToCourse(t.getT2().id()))
  //                .flatMap(c->Mono.empty());
  //    }

}
