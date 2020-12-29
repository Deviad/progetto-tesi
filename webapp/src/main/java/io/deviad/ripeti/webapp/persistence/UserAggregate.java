package io.deviad.ripeti.webapp.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
@Table("users")
@Value
@With
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserAggregate {
  @JsonIgnore
  @Id
  UUID id;
  @Column("username")
  String username;

  @Column("password")
  String password;

  @Column("email")
  String email;

  @Column("first_name")
  String firstName;

  @Column("last_name")
  String lastName;

  @Column("address")
  Address address;

  @Column("role")
  @JsonIgnore
  Role role;



//  public Set<UUID> addCourse(UUID courseId) {
//    if(role == null) {
//      throw new IllegalArgumentException("You must be either a teacher or a student");
//    }
//
//    if(role.name().equals(Role.STUDENT.name())) {
//      throw new IllegalArgumentException("Only teachers can add new courses");
//    }
//    courseIds.add(courseId);
//    return courseIds;
//  }
//
//  public Set<UUID> removeCourse(UUID courseId) {
//    if(role == null) {
//      throw new IllegalArgumentException("You must be either a teacher or a student");
//    }
//
//    if(role.name().equals(Role.STUDENT.name())) {
//      throw new IllegalArgumentException("Only teachers can add remove courses");
//    }
//    courseIds.remove(courseId);
//    return courseIds;
//  }

//  public Set<UUID> enrollInCourse(UUID courseId) {
//    if(role == null) {
//      throw new IllegalArgumentException("You must be either a teacher or a student");
//    }
//    if(role.name().equals(Role.TEACHER.name())) {
//      throw new IllegalArgumentException("You must be a student to enroll in a course");
//    }
//    courseIds.add(courseId);
//    return courseIds;
//  }
//
//  public Set<UUID> unenrollFromCourse(UUID courseId) {
//    if(role == null) {
//      throw new IllegalArgumentException("You must be either a teacher or a student");
//    }
//    if(role.name().equals(Role.TEACHER.name())) {
//      throw new IllegalArgumentException("You must be a student to unenroll from a course");
//    }
//    courseIds.remove(courseId);
//    return courseIds;
//  }

}
