package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.ui.queries.AnswerQuery;
import io.deviad.ripeti.webapp.ui.queries.QuestionResponseDto;
import io.deviad.ripeti.webapp.ui.queries.QuizWithResults;
import io.r2dbc.spi.Row;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.BiFunction;

public class QuizAdapters {

  public static BiFunction<Row, Object, QuizWithResults> QUIZ_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var id = row.get("id", UUID.class);
        var quizName = row.get("quiz_name", String.class);
        var quizContent = row.get("quiz_content", String.class);
        var quiz = new QuizWithResults();
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
                .answers(new LinkedHashMap<>())
                .build();
        return result;
      };

  public static BiFunction<Row, Object, AnswerQuery> ANSWER_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var id = row.get("id", UUID.class);
        var title = row.get("title", String.class);
        var value = row.get("value", Boolean.class);
        return AnswerQuery.builder().id(id).title(title).value(value).build();
      };
}
