import {IFormError, ILesson, Nullable} from "../types";


export class Lesson implements ILesson {
  deleted: Nullable<boolean>;
  errors: Nullable<IFormError>;
  id: string;
  lessonContent: string;
  lessonName: string;
  modified: Nullable<boolean>;
  type: "new" | "existing";

  constructor(
    deleted: Nullable<boolean>,
    errors: Nullable<IFormError>,
    id: string, lessonContent: string,
    lessonName: string, modified: Nullable<boolean>,
    type: "new" | "existing") {
    this.deleted = deleted;
    this.errors = errors;
    this.id = id;
    this.lessonContent = lessonContent;
    this.lessonName = lessonName;
    this.modified = modified;
    this.type = type;
  }


}
