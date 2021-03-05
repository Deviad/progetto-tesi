import {WizardStepsState} from "./WizardSteps";
import {ChangeEvent} from "react";
import produce from "immer";

export const changeQuestionTitle = (quizId: string, questionId: string, state: WizardStepsState, setState: Function) =>
    (evt: ChangeEvent<HTMLInputElement>) => {
        setState(produce((draft: WizardStepsState) => {
            const question = draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]

            question.title = evt.target.value;
            question.modified = true;

        }));
    }
