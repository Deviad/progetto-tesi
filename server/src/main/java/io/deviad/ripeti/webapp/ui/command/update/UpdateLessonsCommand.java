package io.deviad.ripeti.webapp.ui.command.update;


import io.deviad.ripeti.webapp.ui.command.LessonCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateLessonsCommand implements LessonCommand {
    Set<Lesson> lessons;

}
