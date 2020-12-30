package io.deviad.ripeti.webapp.application;


import io.deviad.ripeti.webapp.api.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.api.command.UpdateCourseRequest;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
public class CourseCommandService {

    CourseRepository courseRepository;


    @Transactional
    public Mono<CourseAggregate> createCourse(CreateCourseRequest request) {
        return courseRepository.save(CourseAggregate.createCourse(request.courseName(), request.courseDescription(), request.teacherId()));
    }

    @Transactional
    public Mono<CourseAggregate> updateCourse(UpdateCourseRequest request) {
       return courseRepository.findById(request.courseId())
        .onErrorResume(Mono::error)
        .flatMap(x-> courseRepository.save(x.updateCourseInformation(request.courseName(), request.courseDescription())));
    }

//    @Transactional
//    public void deleteCourse() {
//
//    }
//
//
//
//    @Transactional
//    public void assignUserToCourse() {
//
//    }
//
//    @Transactional
//    public void unassignUserFromCourse() {
//
//    }

}
