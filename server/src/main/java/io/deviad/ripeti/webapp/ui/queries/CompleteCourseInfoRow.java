package io.deviad.ripeti.webapp.ui.queries;

import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteCourseInfoRow {
  UUID courseId;
  String courseName;
  String courseDescription;
  CourseStatus courseStatus;
  UUID courseTeacherId;
  UUID quizId;
  String quizName;
  String quizContent;
  String teacherName;
  String teacherEmail;
  String studentEmail;
  String studentFullName;
  String studentUsername;
  UUID lessonId;
  String lessonName;
  String lessonContent;
  UUID questionId;
  String questionTitle;
  UUID answerId;
  String answerTitle;
  Boolean answerValue;
}
