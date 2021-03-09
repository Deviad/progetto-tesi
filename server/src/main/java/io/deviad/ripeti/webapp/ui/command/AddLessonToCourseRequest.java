package io.deviad.ripeti.webapp.ui.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Value
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@With
public class AddLessonToCourseRequest {
  @NotBlank String lessonName;
  @NotBlank String lessonContent;
}
