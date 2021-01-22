package io.deviad.ripeti.webapp.api.command;


import lombok.Value;

import java.util.Set;

@Value(staticConstructor = "of")
public class QuestionDto {
    String title;
    Set<AnswerDto> answers;
}
