package io.deviad.ripeti.webapp.api.command;

import lombok.Value;

@Value(staticConstructor = "of")
public class AnswerDto {
    String title;
    boolean correct;
}
