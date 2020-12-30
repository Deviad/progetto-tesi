package io.deviad.ripeti.webapp.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
@Table("teams")
@Value
@With
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TeamAggregate {
  @Id
  @Column("id")
  UUID id;

  @Column("team_name")
  String teamName;

  @Column("course_id")
  UUID courseId;

  @Column("student_id")
  Set<UUID> studentIds;

  @Column("score")
  Integer score;
}
