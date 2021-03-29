package io.deviad.ripeti.webapp.ui.queries;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;
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
  Map<UUID, AnswerQuery> answers = new LinkedHashMap<>();
}
