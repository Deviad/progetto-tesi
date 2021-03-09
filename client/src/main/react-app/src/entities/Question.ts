import {IAnswer, IFormError, IQuestion, Nullable} from "../types";

export class Question implements IQuestion {
  answers: Record<string, IAnswer>;
  deleted: Nullable<boolean>;
  errors: IFormError;
  id: string;
  modified: Nullable<boolean>;
  title: string;
  type: "new" | "existing";

  constructor(answers: Record<string, IAnswer>,
              deleted: false, errors: IFormError,
              id: string,
              modified: boolean,
              title: string,
              type: "new" | "existing"
  ) {
    this.answers = answers;
    this.deleted = deleted;
    this.errors = errors;
    this.id = id;
    this.modified = modified;
    this.title = title;
    this.type = type;
  }

}
