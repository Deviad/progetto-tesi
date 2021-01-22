package io.deviad.ripeti.webapp.api.queries;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CourseInfo {
  String courseName;
  UUID courseId;
  String courseDescription;
  CourseStatus courseStatus;
  String teacherName;
  UUID teacherId;
}
