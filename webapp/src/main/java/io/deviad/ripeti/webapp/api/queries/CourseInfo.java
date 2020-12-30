package io.deviad.ripeti.webapp.api.queries;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CourseInfo {
  String courseName;
  String courseId;
  String courseDescription;
  String teacherName;
}
