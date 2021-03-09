package io.deviad.ripeti.webapp.ui.command;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Value(staticConstructor = "of")
public class QuestionRequestDto {
  @NotBlank String title;
  Set<@Valid @NotNull AnswerDto> answers;
}
