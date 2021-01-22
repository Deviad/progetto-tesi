package io.deviad.ripeti.webapp.domain.entity;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
@Table("quizzes")
@Data
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class QuizEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("quiz_name")
    private String quizName;

    @Column("course_id")
    private UUID courseId;

    @Column("quiz_content")
    private String quizContent;

    @Column("question_ids")
    private Set<UUID> questionIds = new LinkedHashSet<>();

}
