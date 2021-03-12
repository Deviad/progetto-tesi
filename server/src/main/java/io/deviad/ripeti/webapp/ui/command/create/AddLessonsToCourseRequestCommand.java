package io.deviad.ripeti.webapp.ui.command.create;

import io.deviad.ripeti.webapp.ui.command.LessonCommand;
import lombok.Data;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class AddLessonsToCourseRequestCommand implements LessonCommand {
  Set<Lesson> lessons;
  @Valid
  @Value
  public static class Lesson {
    @NotBlank String lessonName;
    @NotBlank String lessonContent;
  }
}
