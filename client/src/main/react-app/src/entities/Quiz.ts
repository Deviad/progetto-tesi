import {IFormError, IQuestion, IQuiz} from "../types";


export class Quiz implements IQuiz {
  deleted: boolean;
  errors: IFormError;
  id: string;
  modified: boolean;
  questions: Record<string, IQuestion>;
  quizContent: string;
  quizName: string;
  type: "new" | "existing";

  constructor(
    deleted: boolean,
    errors: IFormError,
    id: string,
    modified: boolean,
    questions: Record<string, IQuestion>,
    quizContent: string,
    quizName: string, type: "new" | "existing") {
    this.deleted = deleted;
    this.errors = errors;
    this.id = id;
    this.modified = modified;
    this.questions = questions;
    this.quizContent = quizContent;
    this.quizName = quizName;
    this.type = type;
  }
}
