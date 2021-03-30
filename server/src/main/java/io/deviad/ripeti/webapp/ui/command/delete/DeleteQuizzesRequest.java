package io.deviad.ripeti.webapp.ui.command.delete;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter
@Builder
@With
public class DeleteQuizzesRequest {
  @NotNull Set<@Valid @NotNull UUID> quizzes;
}
