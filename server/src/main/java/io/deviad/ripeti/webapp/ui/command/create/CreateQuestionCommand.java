package io.deviad.ripeti.webapp.ui.command.create;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Value(staticConstructor = "of")
public class CreateQuestionCommand {
  UUID id;
  @NotBlank String title;
  Set<@Valid @NotNull CreateAnswerDto> answers;
}
