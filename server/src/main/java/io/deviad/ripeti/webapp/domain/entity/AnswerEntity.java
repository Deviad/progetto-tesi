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
@Table("answers")
@Data
@With
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AnswerEntity  implements Persistable<UUID> {
  @Id
  @Column("id")
  private UUID id;

  @Column("title")
  String title;

  @Column("correct")
  Boolean correct;

  @Transient
  private boolean newAnswer;

  @Override
  public UUID getId() {
    return id();
  }

  @Override
  @Transient
  public boolean isNew() {
    return this.newAnswer || id == null;
  }

  public AnswerEntity setAsNew() {
    this.newAnswer = true;
    return this;
  }

}
