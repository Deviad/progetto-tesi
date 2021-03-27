import {IAnswer, IFormError, Nullable} from "../types";
import {Default} from "../utils/utils";
import {Exclude} from "class-transformer";


export class Answer implements IAnswer {
  deleted: Nullable<boolean>;

  modified: Nullable<boolean>;

  @Exclude({toPlainOnly: true})
  errors: IFormError;

  type: "new" | "existing";

  id: string;
  title: string;
  value: boolean;



}
