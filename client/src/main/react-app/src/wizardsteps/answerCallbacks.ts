import {WizardStepsState} from "./WizardSteps";
import {RadioChangeEvent} from "antd/lib/radio/interface";
import produce from "immer";
import {ChangeEvent} from "react";

export const changeAnswerValue = (quizId: string, questionId: string, answerId: string, state: WizardStepsState, setState: Function) =>
    (evt: RadioChangeEvent) => {
        setState(produce((draft: WizardStepsState) => {

            draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]
                .answers[answerId]
                .value = evt.target.value;
        }));
    }


export const changeAnswerTitle = (quizId: string, questionId: string, answerId: string, state: WizardStepsState, setState: Function) =>
    (evt: ChangeEvent<HTMLInputElement>) => {
        setState(produce((draft: WizardStepsState) => {

            draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]
                .answers[answerId]
                .title = evt.target.value;
        }));
    }
