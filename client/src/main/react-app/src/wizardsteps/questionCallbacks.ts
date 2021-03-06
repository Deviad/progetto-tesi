import {WizardStepsState} from "./WizardSteps";
import React, {ChangeEvent} from "react";
import produce from "immer";
import {v4 as uuidv4} from "uuid";
import {message} from "antd";
import {omit} from "lodash";

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
export const addQuestion = (quizId: string, questionId: string, state: WizardStepsState, setState: Function) =>
    (evt: React.MouseEvent<HTMLElement>) => {
        setState(produce((draft: WizardStepsState) => {

            const quiz = draft
                .steps[2]
                .quizzes[quizId]

            const questionId = uuidv4();
            const answerId = uuidv4();
            quiz.questions[questionId] = {
                modified: false,
                id: questionId,
                title: "Completeaza",
                answers: {
                    [answerId]: {
                        value: true,
                        id: answerId,
                        title: "Completeaza",
                        modified: false,
                        deleted: false,
                    }
                }
            }
        }));
    }
export const removedQuestion = (quizId: string, questionId: string, state: WizardStepsState, setState: Function) =>
    (evt: React.MouseEvent<HTMLElement>) => {

        if ((Object.keys(state.steps[2]?.quizzes[quizId]?.questions || {})).length === 1) {
            message.error('Trebuie sa fie cel putin o intrebare');
        } else {
            setState(produce((draft: WizardStepsState) => {
                const questions = draft
                    .steps[2]
                    .quizzes[quizId]
                    .questions;

                draft
                    .steps[2]
                    .quizzes[quizId]
                    .questions = omit(questions, questionId)
            }));
        }
    }
