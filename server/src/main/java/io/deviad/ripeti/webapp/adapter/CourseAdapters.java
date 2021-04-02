package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import io.deviad.ripeti.webapp.ui.queries.CompleteCourseInfo;
import io.deviad.ripeti.webapp.ui.queries.CompleteCourseInfoRow;
import io.deviad.ripeti.webapp.ui.queries.CourseInfo;
import io.r2dbc.spi.Row;

import java.util.UUID;
import java.util.function.BiFunction;

public class CourseAdapters {

  public static BiFunction<Row, Object, CourseInfo> COURSEINFO_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var courseName = row.get("course_name", String.class);
        var courseId = row.get("id", UUID.class);
        var courseDescription = row.get("description", String.class);
        var status = row.get("status", CourseStatus.class);
        var teacherId = row.get("teacher_id", UUID.class);
        var teacherName = row.get("teacher_name", String.class);

        return CourseInfo.builder()
            .courseName(courseName)
            .courseDescription(courseDescription)
            .courseId(courseId)
            .courseStatus(status)
            .teacherId(teacherId)
            .teacherName(teacherName)
            .build();
      };

    public static BiFunction<Row, Object, CompleteCourseInfoRow> COMPLETE_COURSEINFO_FROM_ROW_MAP =

           (Row row, Object o) -> {

                var courseId = row.get("course_id", UUID.class);
                var courseName = row.get("course_name", String.class);
                var courseDescription = row.get("course_description", String.class);
                var courseStatus = row.get("course_status", CourseStatus.class);
                var courseTeacherId = row.get("course_teacher_id", UUID.class);
                var quizId = row.get("quiz_id", UUID.class);
                var quizName = row.get("quiz_name", String.class);
                var quizContent = row.get("quiz_content", String.class);
                var teacherName = row.get("teacher_full_name", String.class);
                var teacherEmail = row.get("teacher_email", String.class);
                var studentEmail = row.get("student_email", String.class);
                var studentFullName = row.get("student_full_name", String.class);
                var studentUsername = row.get("student_username", String.class);
                var lessonId = row.get("lesson_id", UUID.class);
                var lessonName = row.get("lesson_name", String.class);
                var lessonContent = row.get("lesson_content", String.class);
                var questionId = row.get("question_id", UUID.class);
                var questionTitle = row.get("question_title", String.class);
                var answerId = row.get("answer_id", UUID.class);
                var answerTitle = row.get("answer_title", String.class);
                var answerValue = row.get("answer_value", Boolean.class);

                return CompleteCourseInfoRow.builder()
                        .courseId(courseId)
                        .courseName(courseName)
                        .courseDescription(courseDescription)
                        .courseStatus(courseStatus)
                        .courseTeacherId(courseTeacherId)
                        .quizId(quizId)
                        .quizName(quizName)
                        .quizContent(quizContent)
                        .teacherName(teacherName)
                        .teacherEmail(teacherEmail)
                        .studentEmail(studentEmail)
                        .studentFullName(studentFullName)
                        .studentUsername(studentUsername)
                        .lessonId(lessonId)
                        .lessonName(lessonName)
                        .lessonContent(lessonContent)
                        .questionId(questionId)
                        .questionTitle(questionTitle)
                        .answerId(answerId)
                        .answerTitle(answerTitle)
                        .answerValue(answerValue)
                        .teacherName(teacherName)
                        .teacherEmail(teacherEmail)
                        .build();
            };
}
