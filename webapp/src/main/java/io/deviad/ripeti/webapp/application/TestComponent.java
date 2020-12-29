package io.deviad.ripeti.webapp.application;

import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.persistence.CourseAggregate;
import io.deviad.ripeti.webapp.persistence.UserAggregate;
import io.deviad.ripeti.webapp.persistence.repository.CourseRepository;
import io.deviad.ripeti.webapp.persistence.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    var course = CourseAggregate.createCourse("History", savedTeacher.id());
    CourseAggregate savedCourse = courseRepository.save(course).block();
    assert savedCourse != null;
    savedCourse.assignStudentToCourse(savedStudent.id());
    courseRepository.save(savedCourse).block();

  }
}
