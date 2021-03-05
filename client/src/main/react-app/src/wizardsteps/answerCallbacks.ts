import {WizardStepsState} from "./WizardSteps";
import {RadioChangeEvent} from "antd/lib/radio/interface";
import produce from "immer";
import {ChangeEvent} from "react";

export const changeAnswerValue = (quizId: string, questionId: string, answerId: string, state: WizardStepsState, setState: Function) =>
    (evt: RadioChangeEvent) => {
        setState(produce((draft: WizardStepsState) => {

            const answer = draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]
                .answers[answerId];
            answer.value = evt.target.value;
            answer.modified = true;
        }));
    }


export const changeAnswerTitle = (quizId: string, questionId: string, answerId: string, state: WizardStepsState, setState: Function) =>
    (evt: ChangeEvent<HTMLInputElement>) => {
        setState(produce((draft: WizardStepsState) => {

            const answer = draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]
                .answers[answerId];
            answer.title = evt.target.value;
            answer.modified = true;
        }));
    }
