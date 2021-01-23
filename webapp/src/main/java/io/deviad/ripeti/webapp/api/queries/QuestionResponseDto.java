package io.deviad.ripeti.webapp.api.queries;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.api.command.AnswerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Accessors(fluent = true)
@Getter
@With
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {
    UUID id;
    String title;
    Set<AnswerDto> answers = new LinkedHashSet<>();
}
