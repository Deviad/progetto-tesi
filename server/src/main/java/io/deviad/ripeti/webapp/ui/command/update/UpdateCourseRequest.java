package io.deviad.ripeti.webapp.ui.command.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter
@Builder
@With
public class UpdateCourseRequest {
  @NotBlank String courseName;
  @NotBlank String courseDescription;
}
