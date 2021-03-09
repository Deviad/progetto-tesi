import {IAnswer, IFormError, Nullable} from "../types";


export class Answer implements IAnswer {
  deleted: Nullable<boolean>;
  modified: Nullable<boolean>;
  errors: IFormError;
  id: string;
  title: string;
  value: boolean;
  type: "new" | "existing";

  constructor(
    deleted: boolean,
    errors: IFormError,
    id: string,
    modified: boolean,
    title: string,
    value: boolean,
    type: "new" | "existing") {
    this.deleted = deleted;
    this.errors = errors;
    this.id = id;
    this.modified = modified;
    this.title = title;
    this.value = value;
    this.type = type;
  }
}
