package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.ui.queries.Lesson;
import io.r2dbc.spi.Row;

import java.util.UUID;
import java.util.function.BiFunction;

public class LessonAdapters {

  public static BiFunction<Row, Object, Lesson> LESSON_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var id = row.get("id", UUID.class);
        var name = row.get("lesson_name", String.class);
        var content = row.get("lesson_content", String.class);
        return Lesson.builder().id(id).lessonName(name).lessonContent(content).build();
      };
}
