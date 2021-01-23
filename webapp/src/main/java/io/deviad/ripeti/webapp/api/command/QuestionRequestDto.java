package io.deviad.ripeti.webapp.api.command;


import lombok.Value;

import java.util.Set;

@Value(staticConstructor = "of")
public class QuestionRequestDto {
    String title;
    Set<AnswerDto> answers;
}
