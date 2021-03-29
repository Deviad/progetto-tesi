package io.deviad.ripeti.webapp.ui.command.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter
@Builder
@With
public class DeleteLessonsRequest {
  @NotNull List<@Valid @NotNull UUID> lessons;
}
