import {WizardStepsState} from "../../../WizardSteps";
import {RadioChangeEvent} from "antd/lib/radio/interface";
import produce from "immer";
import React, {ChangeEvent} from "react";
import {v4 as uuidv4} from 'uuid';
import {omit} from "lodash";
import {message} from "antd";
import {utils} from "../../../../../utils";
import "../../../../../utils/yupConfig";
import {object, string} from "yup";

export const AnswerSchema = object().shape({
  // eslint-disable-next-line no-template-curly-in-string
  title: string().required().trim().min(3).max(100).test('is-blank', '${path} nu poate fi gol', (value,) => value !== ''),
}).required();


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
      answer.errors = utils.validateFormInput({
        objectToValidate: answer,
        schema: AnswerSchema,
        value: evt.target.value,
        path: "title"
      });
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
        errors: {},
        type: "new",
      }

    }));
  }
export const removeAnswer = (quizId: string, questionId: string, answerId: string, state: WizardStepsState, setState: Function) =>
  async (evt: React.MouseEvent<HTMLElement>) => {

    if ((Object.keys(state.steps[2]?.quizzes[quizId]?.questions[questionId]?.answers) || {}).length === 1) {
      await message.error('Trebuie sa fie cel putin un raspuns');
    } else {
      setState(produce((draft: WizardStepsState) => {

        const answers = draft
          .steps[2]
          .quizzes[quizId]
          .questions[questionId]
          .answers;

        draft
          .steps[2]
          .quizzes[quizId]
          .questions[questionId]
          .answers = omit(answers, answerId)

      }));
    }


  }


