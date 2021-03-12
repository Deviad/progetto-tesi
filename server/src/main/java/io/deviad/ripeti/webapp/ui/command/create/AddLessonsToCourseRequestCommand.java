package io.deviad.ripeti.webapp.ui.command.create;

import io.deviad.ripeti.webapp.ui.command.ILesson;
import io.deviad.ripeti.webapp.ui.command.LessonCommand;
import lombok.Data;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
public class AddLessonsToCourseRequestCommand implements LessonCommand<AddLessonsToCourseRequestCommand.Lesson> {
  List<Lesson> lessons;

  @Valid
  @Value
  public static class Lesson implements ILesson {
    @NotBlank String lessonName;
    @NotBlank String lessonContent;

  }
}
