package io.deviad.ripeti.webapp.ui.command;

import java.util.List;

public interface LessonCommand<T extends ILesson> {

    List<T> getLessons();

}
