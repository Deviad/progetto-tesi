import { Exclude } from "class-transformer";
import {ICourse} from "../features/coursemanagement/WizardSteps";
import {Default} from "../utils/utils";
import {IFormError, Nullable} from "../types";

export class Course implements ICourse {

  id: string;
  title: string;
  description: string;

  @Exclude({toPlainOnly: true})
  errors: Nullable<IFormError>;

  type: "new"|"existing"

  deleted: Nullable<boolean>;

  modified: Nullable<boolean>;

}
