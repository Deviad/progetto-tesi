package io.deviad.ripeti.webapp.ui.command.update;

import java.util.UUID;

public class Lesson {

  UUID id;
  String lessonName;
  String lessonContent;

  public Lesson(UUID uuid, String lessonName, String lessonContent) {
    this.id = uuid;
    this.lessonName = lessonName;
    this.lessonContent = lessonContent;
  }

  public Lesson() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID uuid) {
    this.id = uuid;
  }

  public String getLessonName() {
    return lessonName;
  }

  public void setLessonName(String lessonName) {
    this.lessonName = lessonName;
  }

  public String getLessonContent() {
    return lessonContent;
  }

  public void setLessonContent(String lessonContent) {
    this.lessonContent = lessonContent;
  }
}
