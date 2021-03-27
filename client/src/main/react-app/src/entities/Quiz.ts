import {IFormError, IQuestion, IQuiz} from "../types";
import {Default, MapToList, MapToRecord} from "../utils/utils";
import {Question} from "./Question";
import {Exclude} from "class-transformer";

export class Quiz implements IQuiz {
  deleted: boolean = true;

  modified: boolean;

  @Exclude({toPlainOnly: true})
  errors: IFormError;

  @Default("existing")
  type: "new" | "existing";

  id: string;
  @MapToRecord(Question)
  @MapToList(Question)
  questions: Record<string, IQuestion>;
  quizContent: string;
  quizName: string;

}
