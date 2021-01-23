package io.deviad.ripeti.webapp.api.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

@Builder
@Getter
public class AnswerDto {

    UUID id;
    String title;
    Boolean correct;
}
