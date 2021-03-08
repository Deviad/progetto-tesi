import React, {ChangeEvent} from "react";
import {WizardStepsState} from "../../../WizardSteps";
import {omit} from "lodash";


type QuizNameChanged = (state: WizardStepsState, setState: Function, quizId: string) => (event: ChangeEvent<HTMLInputElement>) => void;

export const quizNameChanged: QuizNameChanged = (state, setState, quizId) => (event) => {

    const [, , step3] = state.steps;

    setState({
        ...state, steps: [...state.steps.slice(0, 2), {
            ...step3,
            quizzes: {
                ...step3.quizzes,
                [quizId]: {
                    ...step3.quizzes[quizId],
                    quizName: event.target.value,
                    modified: true
                }
            }
        }]
    })
}

type QuizDescriptionChanged = (state: WizardStepsState, setState: Function, quizId: string) => (data: string) => void;


export const quizDescriptionChanged: QuizDescriptionChanged = (state, setState, quizId) => (data: string) => {

    const [, , step3] = state.steps;

    setState({
        ...state, steps: [...state.steps.slice(0, 2), {
            ...step3,
            quizzes: {
                ...step3.quizzes,
                [quizId]: {
                    ...step3.quizzes[quizId],
                    quizContent: data,
                    modified: true
                }
            }
        }]
    })
};

type QuizDeleted = (state: WizardStepsState, setState: Function, quizId: string) => ( event: React.MouseEvent<HTMLInputElement>) => void;

export const quizDeleted: QuizDeleted = (state, setState, quizId) =>  () => {

    const [, , step3] = state.steps;

    if (step3.quizzes[quizId].type === "new") {
        setState({
            ...state, steps: [...state.steps.slice(0, 2), {
                ...step3,
                quizzes: {
                    ...omit(step3.quizzes, quizId)
                }
            }]
        })
    } else {
        setState({
            ...state, steps: [...state.steps.slice(0, 2), {
                ...step3,
                quizzes: {
                    ...step3.quizzes,
                    [quizId]: {
                        ...step3.quizzes[quizId],
                        deleted: true
                    }
                }
            }]
        })
    }
}
