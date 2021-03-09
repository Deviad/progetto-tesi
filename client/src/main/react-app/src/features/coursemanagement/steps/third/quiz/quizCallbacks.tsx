import React, {ChangeEvent} from "react";
import {WizardStepsState} from "../../../WizardSteps";
import {omit} from "lodash";
import {utils} from "../../../../../utils";
import {object, string} from "yup";

export const QuizSchema = object().shape({
  // eslint-disable-next-line no-template-curly-in-string
  quizName: string().required().trim().min(3).max(100).test('is-blank', 'Titlu nu poate fi gol', (value,) => value !== ''),
  // eslint-disable-next-line no-template-curly-in-string
  quizContent: string().required().trim().min(3).test('is-blank', '${path} nu poate fi gol', (value,) => value !== ''),

}).required();

type QuizNameChanged = (state: WizardStepsState, setState: Function, quizId: string) => (event: ChangeEvent<HTMLInputElement>) => void;

export const quizNameChanged: QuizNameChanged = (state, setState, quizId) => (event) => {

  const [, , step3] = state.steps;

  const errors = utils.validateFormInput({
    objectToValidate: step3.quizzes[quizId],
    schema: QuizSchema,
    value: event,
    path: "quizName"
  });


  setState({
    ...state, steps: [...state.steps.slice(0, 2), {
      ...step3,
      quizzes: {
        ...step3.quizzes,
        [quizId]: {
          ...step3.quizzes[quizId],
          quizName: event.target.value,
          modified: true,
          errors,
        }
      }
    }]
  })
}

type QuizDescriptionChanged = (state: WizardStepsState, setState: Function, quizId: string) => (data: string) => void;


export const quizDescriptionChanged: QuizDescriptionChanged = (state, setState, quizId) => (data: string) => {

  const [, , step3] = state.steps;

  const errors = utils.validateFormInput({
    objectToValidate: step3.quizzes[quizId],
    schema: QuizSchema,
    value: data,
    path: "quizContent"
  });

  setState({
    ...state, steps: [...state.steps.slice(0, 2), {
      ...step3,
      quizzes: {
        ...step3.quizzes,
        [quizId]: {
          ...step3.quizzes[quizId],
          quizContent: data,
          modified: true,
          errors
        }
      }
    }]
  })
};

type QuizDeleted = (state: WizardStepsState, setState: Function, quizId: string) => (event: React.MouseEvent<HTMLInputElement>) => void;

export const quizDeleted: QuizDeleted = (state, setState, quizId) => () => {

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
