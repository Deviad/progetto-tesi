package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.api.command.AnswerDto;
import io.deviad.ripeti.webapp.api.queries.QuestionResponseDto;
import io.deviad.ripeti.webapp.api.queries.QuizWithoutResults;
import io.r2dbc.spi.Row;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.function.BiFunction;

public class QuizAdapters {

  public static BiFunction<Row, Object, QuizWithoutResults> QUIZ_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var id = row.get("id", UUID.class);
        var quizName = row.get("quiz_name", String.class);
        var quizContent = row.get("quiz_content", String.class);
        var quiz = new QuizWithoutResults();
        quiz = quiz.withId(id).withQuizName(quizName).withQuizContent(quizContent);
        return quiz;
      };

  public static BiFunction<Row, Object, QuestionResponseDto> QUESTION_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var id = row.get("id", UUID.class);
        var title = row.get("title", String.class);
        var result =
            QuestionResponseDto.builder()
                .id(id)
                .title(title)
                .answers(new LinkedHashSet<>())
                .build();
        return result;
      };

  public static BiFunction<Row, Object, AnswerDto> ANSWER_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var id = row.get("id", UUID.class);
        var title = row.get("title", String.class);
        var correct = row.get("correct", Boolean.class);
        return AnswerDto.builder().id(id).title(title).correct(correct).build();
      };
}
