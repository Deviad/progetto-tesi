import React, {ChangeEvent} from "react";
import produce from "immer";
import {v4 as uuidv4} from "uuid";
import {message} from "antd";
import {omit} from "lodash";
import {WizardStepsState} from "../../../WizardSteps";
import {utils} from "../../../../../utils";
import {FirstStepSchema} from "../../first/firstStepCallbacks";
import {object, string} from "yup";

export const QuestionSchema = object().shape({
    // eslint-disable-next-line no-template-curly-in-string
    title: string().required().trim().min(3).max(100).test('is-blank', 'Titlu nu poate fi gol', (value,)=> value !== ''),
}).required();


export const changeQuestionTitle = (quizId: string, questionId: string, state: WizardStepsState, setState: Function) =>
    (evt: ChangeEvent<HTMLInputElement>) => {
        setState(produce((draft: WizardStepsState) => {
            const question = draft
                .steps[2]
                .quizzes[quizId]
                .questions[questionId]

            utils.validateFormInput({
                objectToValidate: question,
                schema: FirstStepSchema,
                value: evt,
                path: "title"});

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
                        type: "new",
                        errors: {},
                    }
                },
                type: "new",
                errors: {},
                deleted: false,
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
