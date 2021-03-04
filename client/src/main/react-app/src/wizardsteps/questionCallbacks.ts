import {WizardStepsState} from "./WizardSteps";
import {ChangeEvent} from "react";
import produce from "immer";

export const changeQuestionTitle = (quizId: string, questionId: string, state: WizardStepsState, setState: Function) =>
    (evt: ChangeEvent<HTMLInputElement>) => {
        setState(produce((draft: WizardStepsState) => {
            draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]
                .title = evt.target.value;
        }));
    }
