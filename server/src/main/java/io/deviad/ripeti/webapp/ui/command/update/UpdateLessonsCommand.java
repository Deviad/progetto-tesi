package io.deviad.ripeti.webapp.ui.command.update;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.deviad.ripeti.webapp.ui.command.ILesson;
import io.deviad.ripeti.webapp.ui.command.LessonCommand;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


// merge cu si fara @AllArgsConstructor

@ToString
@EqualsAndHashCode
@Getter
@Builder
@With
public class UpdateLessonsCommand implements LessonCommand<UpdateLessonsCommand.Lesson> {
    @JsonProperty("lessons")
    List<Lesson> lessons;

    @ToString
    @EqualsAndHashCode
    @Getter
    @Builder
    @With
    public static  class Lesson implements ILesson {
        @JsonProperty("id")
        @NotNull UUID id;
        @JsonProperty("lessonName")
        @NotBlank String lessonName;
        @JsonProperty("lessonContent")
        @NotBlank String lessonContent;

    }

}
