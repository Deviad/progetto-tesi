package io.deviad.ripeti.webapp.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.course.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
@Table("courses")
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CourseAggregate {

  public static final String DOES_NOT_EXIST_YET = "You cannot assign a %s to a course that does not exist yet";
  public static final String NOT_PUBLISHED_YET = "You cannot assign a %s to a course that is not published yet";

  @Id
  @Column("id")
  private UUID id;

  @Column("course_name")
  private final String courseName;

  @Column("description")
  private String description;

  @Column("status")
  private Status status;

  @Column("teacher_id")
  private final UUID teacherId;

  @Column("student_ids")
  private Set<UUID> studentIds = new LinkedHashSet<>();

  @Column("lesson_ids")
  private Set<UUID> lessonIds = new LinkedHashSet<>();

  private CourseAggregate(String courseName, String description, UUID teacherId) {
    this.courseName = courseName;
    this.description = description;
    this.teacherId = teacherId;
    this.status = Status.DRAFT;
  }

  public static CourseAggregate createCourse(
      String courseName, String description, UUID teacherId) {
    return new CourseAggregate(courseName, description, teacherId);
  }

  public  CourseAggregate updateCourseInformation(String courseName, String courseDescription) {
    return changeCourseName(courseName).changeCourseDescription(courseDescription);
  }

  public CourseAggregate publishCourse() {
    return new CourseAggregate(
        id(), courseName(), description(), Status.LIVE, teacherId(), studentIds(), lessonIds());
  }

  public CourseAggregate assignStudentToCourse(UUID student) {
    if (id == null) {
      throw new RuntimeException(String.format(DOES_NOT_EXIST_YET, "student"));
    }

    if (status().name().equals(Status.DRAFT.name())) {
      throw new RuntimeException(String.format(NOT_PUBLISHED_YET, "student"));
    }

    studentIds.add(student);
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
        id(), courseName(), description(), status(), teacherId(), studentIds(), lessonIds());
  }

  public CourseAggregate changeCourseDescription(String description) {
    if (id == null) {
      throw new RuntimeException(
              String.format(DOES_NOT_EXIST_YET, "description"));
    }
    return new CourseAggregate(
        id(), courseName(), description(), status(), teacherId(), studentIds(), lessonIds());
  }
}
