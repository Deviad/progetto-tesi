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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
@Table("questions")
@Data
@With
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class QuestionEntity implements Persistable<UUID> {
  @Id
  @Column("id")
  private UUID id;

  @Column("title")
  private String title;

  @Column("answer_ids")
  private Set<UUID> answerIds = new LinkedHashSet<>();

  @Transient
  boolean newQuestion;

  @Override
  public UUID getId() {
    return id();
  }

  @Override
  @Transient
  public boolean isNew() {
    return this.newQuestion || id == null;
  }

  public QuestionEntity setAsNew() {
    this.newQuestion = true;
    return this;
  }
}
