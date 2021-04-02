package io.deviad.ripeti.webapp.ui.queries;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompleteCourseInfo {
  String courseName;
  UUID courseId;
  String courseDescription;
  CourseStatus courseStatus;
  String teacherName;
  String teacherEmail;
  UUID teacherId;
  Map<UUID, QuizWithResults> quizzes = new HashMap<>();
  Set<Student> studentList = new HashSet<>();

}
