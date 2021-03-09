import {IFormError, ILesson, IQuiz, Nullable} from "../../types";
import {utils} from "../../utils";
import React, {SyntheticEvent} from "react";
import {message} from "antd";
import {FirstStepSchema} from "./steps/first/firstStepCallbacks";
import {kebabCase} from "lodash";
import {SecondStepSchema} from "./steps/second/secondStepCallbacks";
import {QuizSchema} from "./steps/third/quiz/quizCallbacks";
import {QuestionSchema} from "./steps/third/question/questionCallbacks";
import {AnswerSchema} from "./steps/third/answer/answerCallbacks";
import {WizardStepsState} from "./WizardSteps";

export const validateStepBeforeMovingForward = async (errors: IFormError) => {
  if (utils.isTrue(errors) && Object.keys(errors).length > 0) {
    let acc = [] as React.ReactNodeArray;
    for (const [eKey, eVal] of Object.entries(errors)) {
      if (typeof eVal === "string") {
        acc.push(<>{eVal} <br/></>);
      } else if (Object.keys(eVal).length && Object.keys(eVal).length > 0) {
        Object.values(eVal).forEach(val => acc.push(<>{eKey} - {val} <br/></>));
      }
    }
    if (acc.length > 0) {
      await message.error(Object.values(acc).map(x => x));
      return;
    }
  }
}

export const handleStep1 = async (errors: IFormError, state: WizardStepsState) => {
  if (state.currentStep === 0) {
    const latestErrors = utils.validateFormBlock(state.steps[state.currentStep].content, FirstStepSchema);

    for (const [key, value] of Object.entries(latestErrors)) {
      errors[key] = value;
    }

  }
}

export const handleStep2 = async (errors: IFormError, state: WizardStepsState) => {
  const usedNames: string[] = [];
  for (const [, lesson] of Object.entries((state.steps[1].lessons as Record<string, ILesson>))) {
    if (usedNames.some(l => l === kebabCase(lesson.lessonName.toLowerCase()))) {
      await message.error("Nu poti avea 2 lecti cu aceasi denumire");
      return;
    }
    errors[kebabCase(lesson.lessonName.toLowerCase())] = utils.validateFormBlock(lesson, SecondStepSchema);
    usedNames.push(kebabCase(lesson.lessonName.toLowerCase()));

  }
}

export const handleStep3 = async (errors: IFormError, state: WizardStepsState) => {
  //TODO: verify that the same name is not used twice as I did for step 2 (step[1]).
  if (state.currentStep === 2) {
    for (const quizValue of Object.values(state.steps[2].quizzes as Record<string, IQuiz>)) {
      errors[kebabCase(quizValue.quizName.toLowerCase())] = utils.validateFormBlock(quizValue, QuizSchema);
      for (const question of Object.values(quizValue.questions)) {
        errors[`${kebabCase(quizValue.quizName.toLowerCase())}-${kebabCase(question.title.toLowerCase())}`] =
          utils.validateFormBlock(question, QuestionSchema)

        for (const answer of Object.values(question.answers)) {
          errors[`${kebabCase(quizValue.quizName.toLowerCase())}-${kebabCase(question.title.toLowerCase())}-${kebabCase(answer.title.toLowerCase())}`] =
            utils.validateFormBlock(answer, AnswerSchema)
        }
      }
    }
  }
}

export const next = (state: WizardStepsState, setState: Function) => async (event: SyntheticEvent<HTMLFormElement>) => {

  const stepHandlers = [handleStep1, handleStep2, handleStep3];

  let errors: Nullable<Record<string, any>> = {};

  for (const fn of stepHandlers) {
    await fn(errors, state);
  }

  await validateStepBeforeMovingForward(errors);

  if (Object.values(errors).filter(x=> Object.keys(x).length > 0).length > 0) {
   return;
  }

  // 2 is the final step (step3)
  if (state.currentStep !== 2) {
    setState({...state, currentStep: state.currentStep + 1});

  } else {
    message.error("Hurra!");
  }

};
