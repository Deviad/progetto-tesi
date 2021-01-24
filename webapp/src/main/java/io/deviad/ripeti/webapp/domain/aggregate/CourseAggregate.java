package io.deviad.ripeti.webapp.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
@Table("courses")
@With
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseAggregate {

  public static final String DOES_NOT_EXIST_YET =
      "You cannot assign a %s to a course that does not exist yet";
  public static final String NOT_PUBLISHED_YET =
      "You cannot assign a %s to a course that is not published yet";

  @Id
  @Column("id")
  private UUID id;

  @Column("course_name")
  private String courseName;

  @Column("description")
  private String description;

  @Column("status")
  private CourseStatus status;

  @Column("teacher_id")
  private UUID teacherId;

  @Column("student_ids")
  private Set<UUID> studentIds = new LinkedHashSet<>();

  @Column("lesson_ids")
  private Set<UUID> lessonIds = new LinkedHashSet<>();

  @Column("quiz_ids")
  private Set<UUID> quizIds = new LinkedHashSet<>();

  private CourseAggregate(String courseName, String description, UUID teacherId) {
    this.courseName = courseName;
    this.description = description;
    this.teacherId = teacherId;
    this.status = CourseStatus.DRAFT;
  }

  public static CourseAggregate createCourse(
      String courseName, String description, UUID teacherId) {
    return new CourseAggregate(courseName, description, teacherId);
  }

  public CourseAggregate updateCourseInformation(String courseName, String courseDescription) {
    return changeCourseName(courseName).changeCourseDescription(courseDescription);
  }

  public CourseAggregate publishCourse() {
    return new CourseAggregate(
        id(),
        courseName(),
        description(),
        CourseStatus.LIVE,
        teacherId(),
        studentIds(),
        lessonIds(),
        quizIds());
  }

  public CourseAggregate assignStudentToCourse(UUID student) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "student"));
    }

    if (status().name().equals(CourseStatus.DRAFT.name())) {
      throw new RuntimeException(String.format(NOT_PUBLISHED_YET, "student"));
    }

    studentIds.add(student);
    return this;
  }

  public CourseAggregate removeStudentFromCourse(UUID student) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "student"));
    }

    if (status().name().equals(CourseStatus.DRAFT.name())) {
      throw new RuntimeException(String.format(NOT_PUBLISHED_YET, "student"));
    }

    studentIds.remove(student);
    return this;
  }

  public CourseAggregate addLessonToCourse(UUID lesson) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "lesson"));
    }
    lessonIds.add(lesson);
    return this;
  }

  public CourseAggregate changeCourseName(String name) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "course"));
    }
    return new CourseAggregate(
        id(), name, description(), status(), teacherId(), studentIds(), lessonIds(), quizIds());
  }

  public CourseAggregate changeCourseDescription(String description) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "description"));
    }
    return new CourseAggregate(
        id(),
        courseName(),
        description,
        status(),
        teacherId(),
        studentIds(),
        lessonIds(),
        quizIds());
  }

  public CourseAggregate addQuizToCourse(UUID quiz) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "quiz"));
    }
    quizIds.add(quiz);
    return this;
  }
}
