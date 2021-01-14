package io.deviad.ripeti.webapp.api.queries;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value(staticConstructor = "of")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CourseInfo {
  String courseName;
  String courseId;
  String courseDescription;
  String teacherName;
}
