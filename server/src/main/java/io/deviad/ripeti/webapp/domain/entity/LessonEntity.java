package io.deviad.ripeti.webapp.domain.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Accessors(fluent = true)
@Table("lessons")
@Data
@With
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class LessonEntity implements Persistable<UUID> {

  @Id
  @Column("id")
  private UUID id;

  @Column("lesson_name")
  private String lessonName;

  @Column("lesson_content")
  private String lessonContent;

  @Override
  public UUID getId() {
    return id();
  }

  @Transient
  boolean newLesson;

  @Override
  @Transient
  public boolean isNew() {
    return this.newLesson || id == null;
  }

  public LessonEntity setAsNew() {
    this.newLesson = true;
    return this;
  }
}
