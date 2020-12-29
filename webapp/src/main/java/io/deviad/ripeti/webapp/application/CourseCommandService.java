package io.deviad.ripeti.webapp.application;


import io.deviad.ripeti.webapp.adapter.CourseAdapters;
import io.deviad.ripeti.webapp.api.command.CreateCourseRequest;
import io.deviad.ripeti.webapp.persistence.CourseAggregate;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CourseCommandService {

    CourseRepository courseRepository;


    @Transactional
    public void createCourse(CreateCourseRequest request) {
        courseRepository.save(CourseAggregate.createCourse(request.courseName(), request.teacherId()));
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
