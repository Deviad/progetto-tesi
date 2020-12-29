package io.deviad.ripeti.webapp.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
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
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CourseAggregate {
    @Id
    @Column("id")
    private UUID id;
    @Column("course_name")
    private final String courseName;
    @Column("teacher_id")
    private final UUID teacherId;
    @Column("student_ids")
    private Set<UUID> studentIds = new LinkedHashSet<>();
    @Column("lesson_ids")
    private Set<UUID> lessonIds = new LinkedHashSet<>();

    private CourseAggregate(String courseName, UUID teacherId) {
        this.courseName = courseName;
        this.teacherId = teacherId;
    }


    public static CourseAggregate createCourse(String courseName, UUID teacherId) {
       return new CourseAggregate(courseName, teacherId);
    }


    public CourseAggregate assignStudentToCourse(UUID student) {
       if(id == null) {
           throw new RuntimeException("You cannot assign a student to a course that does not exist yet");
       }

       studentIds.add(student);
       return this;
    }

    public CourseAggregate addLessonToCourse(UUID lesson) {
        if(id == null) {
            throw new RuntimeException("You cannot add a lesson to a course that does not exist yet");
        }
        lessonIds.add(lesson);
        return this;
    }

    public CourseAggregate changeCourseName(String name) {
        if(id == null) {
            throw new RuntimeException("You cannot modify the name of a course that does not exist yet");
        }
      return new CourseAggregate(id(), courseName(), teacherId(), studentIds(), lessonIds());
    }

}
