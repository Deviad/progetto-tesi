package io.deviad.ripeti.webapp.application;

import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TestComponent implements InitializingBean {
  @Autowired UserRepository userRepository;

  @Autowired CourseRepository courseRepository;

  @Override
  public void afterPropertiesSet() throws Exception {
    var student =
        UserAggregate.builder()
            .firstName("Ciccione")
            .lastName("pizzo")
            .username("helloworld")
            .email("asdas@gmail.com")
            .password("Helloworld@12341@")
            .role(Role.STUDENT)
            .address(
                Address.builder()
                    .city("Bucharest")
                    .country("Test")
                    .firstAddressLine("Testasdsa")
                    .secondAddressLine("Testasdas")
                    .build())
            .build();
    UserAggregate savedStudent = userRepository.save(student).block();

    var teacher =
        UserAggregate.builder()
            .firstName("Teacher")
            .lastName("Hello")
            .username("teacherhello")
            .password("Helloworld@12341@")
            .email("whatever@gmail.com")
            .role(Role.TEACHER)
            .address(
                Address.builder()
                    .city("Bucharest")
                    .country("Test")
                    .firstAddressLine("Testasdsa")
                    .secondAddressLine("Testasdas")
                    .build())
            .build();
    UserAggregate savedTeacher = userRepository.save(teacher).block();

    var course = CourseAggregate.createCourse("History", "testttt", savedTeacher.id());
    var course2 = CourseAggregate.createCourse("History2", "testttt2", savedTeacher.id());
    CourseAggregate savedCourse = courseRepository.save(course).block();
    CourseAggregate savedCourse2 = courseRepository.save(course2).block();

    Objects.requireNonNull(savedCourse, "Save should return a course");
    Objects.requireNonNull(savedCourse2, "Save should return a course");

    CourseAggregate publishedCourse1 = savedCourse.publishCourse();
    CourseAggregate publishCourse2 = savedCourse2.publishCourse();

    CourseAggregate saved2Course1 = courseRepository.save(publishedCourse1).block();
    CourseAggregate saved2Course2 = courseRepository.save(publishCourse2).block();

    assert saved2Course1 != null;
    saved2Course1.assignStudentToCourse(savedStudent.id());
    assert saved2Course2 != null;
    saved2Course2.assignStudentToCourse(savedStudent.id());


    courseRepository.save(saved2Course1).block();
    courseRepository.save(saved2Course2).block();



  }
}
