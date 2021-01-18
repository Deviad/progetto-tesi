package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.api.queries.CourseInfo;
import io.deviad.ripeti.webapp.domain.valueobject.course.Status;
import io.r2dbc.spi.Row;

import java.util.UUID;
import java.util.function.BiFunction;

public class CourseAdapters {

    public static BiFunction<Row, Object, CourseInfo> COURSEINFO_FROM_ROW_MAP =
            (Row row, Object o) -> {
                var courseName = row.get("course_name", String.class);
                var courseId = row.get("id", UUID.class);
                var courseDescription = row.get("description", String.class);
                var status = row.get("status", Status.class);
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


}
