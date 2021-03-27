import {IFormError, ILesson, Nullable} from "../types";
import {Default} from "../utils/utils";
import {Exclude} from "class-transformer";


export class Lesson implements ILesson {
  deleted: Nullable<boolean>;

  @Exclude({toPlainOnly: true})
  errors: Nullable<IFormError>;

  type: "new" | "existing";

  modified: Nullable<boolean>;

  id: string;
  lessonName: string;
  lessonContent: string;


}
