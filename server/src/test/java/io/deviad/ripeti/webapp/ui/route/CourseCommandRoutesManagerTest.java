package io.deviad.ripeti.webapp.ui.route;

import io.deviad.ripeti.webapp.application.command.CourseCommandService;
import io.deviad.ripeti.webapp.application.command.LessonCommandService;
import io.deviad.ripeti.webapp.application.command.UserCommandService;
import io.deviad.ripeti.webapp.domain.aggregate.CourseAggregate;
import io.deviad.ripeti.webapp.infrastructure.OAuth2ResourceServerConfig;
import io.deviad.ripeti.webapp.ui.command.create.CreateCourseRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ContextConfiguration(
    classes = {
      CourseCommandRoutesManager.class,
      OAuth2ResourceServerConfig.class,
    })
@WebFluxTest
class CourseCommandRoutesManagerTest {
  @Autowired private ApplicationContext context;

  private WebTestClient webTestClient;
  @MockBean
  private CourseCommandService courseService;
  @MockBean
  private UserCommandService userCommandService;
  @MockBean
  private LessonCommandService lessonCommandService;

  @BeforeEach
  void setup() {
    webTestClient = WebTestClient.bindToApplicationContext(context).apply(springSecurity()).configureClient().filter(basicAuthentication()).build();
  }


  @Test
  void createCourse() {
    final CourseAggregate course =
        CourseAggregate.builder()
            .id(UUID.fromString("68c58ba-c5f6-4645-9e6c-58d698807dd8"))
            .courseName("Name")
            .teacherId(UUID.fromString("68c58ba-c5f6-4645-9e6c-58d698807dd8"))
            .studentIds(new HashSet<>(Collections.singletonList(UUID.fromString("68c58ba-c5f6-4645-9e6c-58d698807dd8"))))
            .description("Description")
            .quizIds(
                new HashSet<>(
                    Collections.singletonList(
                        UUID.fromString("276c56be-f6ad-4627-b44f-7dd44102a0eb"))))
            .build();
    final CreateCourseRequest courseRequest = new CreateCourseRequest("Name", "Description");
    lenient().doReturn(Mono.just(course)).when(courseService).createCourse(any(), any());

    webTestClient.
    mutateWith(mockJwt())
        .post()
        .uri("/api/course")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(courseRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(CourseAggregate.class)
        .value(
            c -> {
              Assertions.assertThat(c.id())
                  .isEqualTo(UUID.fromString("68c58ba-c5f6-4645-9e6c-58d698807dd8"));
              Assertions.assertThat(c.courseName()).isEqualTo("Name");
              Assertions.assertThat(c.description()).isEqualTo("Description");
              assertArrayEquals(
                  c.quizIds().toArray(),
                  new UUID[] {UUID.fromString("276c56be-f6ad-4627-b44f-7dd44102a0eb")});
            });
  }

  @Test
  void deleteCourse() {


    lenient().doReturn(Mono.empty()).when(courseService).deleteCourse(any(), any());

    webTestClient
        .mutateWith(mockJwt())
        .delete()
        .uri("/api/course/276c56be-f6ad-4627-b44f-7dd44102a0eb/deletecourse")
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void handleUpdate() {}

  @Test
  void assignStudentToCourse() {}

  @Test
  void unassignUserFromCourse() {}

  @Test
  void publishCourse() {}

  @Test
  void addLessonsToCourse() {}

  @Test
  void updateLessons() {}

  @Test
  void removeLessons() {}

  @Test
  void createOrUpdateQuiz() {}

  @Test
  void removeQuizzes() {}
}
