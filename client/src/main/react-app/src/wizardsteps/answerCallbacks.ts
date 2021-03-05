import {WizardStepsState} from "./WizardSteps";
import {RadioChangeEvent} from "antd/lib/radio/interface";
import produce from "immer";
import React, {ChangeEvent} from "react";
import {v4 as uuidv4} from 'uuid';

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

export const addAnswer = (quizId: string, questionId: string, state: WizardStepsState, setState: Function) =>
    (evt: React.MouseEvent<HTMLElement>) => {
        setState(produce((draft: WizardStepsState) => {

            const answers = draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]
                .answers;

            const id = uuidv4();
            answers[id] = {
                title: "Introduci titlu",
                id,
                deleted: false,
                modified: false,
                value: false,
            }

        }));
    }



