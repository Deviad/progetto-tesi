package io.deviad.ripeti.webapp.ui.command.create;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Value
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CreateCourseRequest {

  @NotBlank String courseName;
  @NotBlank String courseDescription;
}
