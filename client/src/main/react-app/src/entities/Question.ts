import {IAnswer, IFormError, IQuestion, Nullable} from "../types";
import {Exclude, Type} from "class-transformer";
import {Default, MapToList, MapToRecord} from "../utils/utils";
import {Answer} from "./Answer";

export class Question implements IQuestion {

  @Type(()=> Answer)
  @MapToRecord(Answer)
  @MapToList(Answer)
  answers: Record<string, IAnswer>;

  deleted: Nullable<boolean>;

  modified: Nullable<boolean>;

  type: "new" | "existing";

  @Exclude({toPlainOnly: true})
  errors: IFormError;
  id: string;

  title: string;

}
