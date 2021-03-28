import {WizardStepsState} from "../../WizardSteps";
import React, {ChangeEvent} from "react";
import {v4 as uuidv4} from "uuid";
import {omit} from "lodash";
import {utils} from "../../../../utils";
import {object, string} from "yup";
import {message} from "antd";
import "../../../../utils/yupConfig"

type LessonNameChangedProps = {
  state: WizardStepsState;
  setState: Function;
  id?: string;
};
export const SecondStepSchema = object().shape({
  // eslint-disable-next-line no-template-curly-in-string
  lessonName: string().required().min(3).max(100).test('is-blank', '${path} nu poate fi gol', (value,) => value !== ''),
  // eslint-disable-next-line no-template-curly-in-string
  lessonContent: string().required().min(3).test('is-blank', '${path} nu poate fi gol', (value,) => value !== ''),
}).required();
type LessonNameChanged = (props: LessonNameChangedProps) => (event: ChangeEvent<HTMLInputElement>) => void;


export const lessonNameChangeChanged: LessonNameChanged =
  ({state, setState, id}) => (event) => {

    const step2 = state.steps[1];

    if (id) {
      const currentLesson = step2.lessons[id];

      const errors = utils.validateFormInput({
        objectToValidate: currentLesson,
        schema: SecondStepSchema,
        value: event,
        path: "lessonName"
      });

      setState({
        ...state, steps: [...state.steps.slice(0, 1), {
          ...step2,
          lessons: {
            ...step2.lessons,
            [id]: {
              ...step2.lessons[id],
              lessonName: event.target.value,
              modified: true,
              errors,
            }
          },
        }, ...state.steps.slice(2)]
      })
    } else {

      const errors = utils.validateFormInput({
        objectToValidate: step2.newLesson,
        schema: SecondStepSchema,
        value: event,
        path: "lessonName"
      });

      setState({
        ...state, steps: [...state.steps.slice(0, 1), {
          ...step2,
          newLesson: {
            ...step2.newLesson,
            lessonName: event.target.value,
            errors,
          }
        }, ...state.steps.slice(2)]
      });
    }
  }

type LessonContentChangedProps = { state: WizardStepsState; setState: Function; id?: string; }

type LessonContentChanged = (props: LessonContentChangedProps) => (data: string) => void;

export const lessonContentChanged: LessonContentChanged =
  ({state, setState, id}) => (data) => {
    const step2 = state.steps[1];

    if (id) {
      const currentLesson = step2.lessons[id];

      const errors = utils.validateFormInput({
        objectToValidate: currentLesson,
        schema: SecondStepSchema,
        value: utils.stripHtmlTags(data),
        path: "lessonContent"
      });
      setState({
        ...state, steps: [...state.steps.slice(0, 1), {
          ...step2,
          lessons: {
            ...step2.lessons,
            [id]: {
              ...step2.lessons[id],
              lessonContent: data,
              modified: true,
              errors,
            }
          }
        }, ...state.steps.slice(2)]
      })
    } else {

      const errors = utils.validateFormInput({
        objectToValidate: step2.newLesson,
        schema: SecondStepSchema,
        value: utils.stripHtmlTags(data),
        path: "lessonContent"
      });

      setState({
        ...state, steps: [...state.steps.slice(0, 1), {
          ...step2,
          newLesson: {
            ...step2.newLesson,
            lessonContent: data,
            errors,
          },
        }, ...state.steps.slice(2)]
      });
    }
  }


type LessonDataAddedProps = {
  state: WizardStepsState;
  setState: Function;
}

type LessonDataAdded = (props: LessonDataAddedProps) => (event: React.MouseEvent<HTMLElement>) => Promise<void>;

export const lessonDataAdded: LessonDataAdded =
  ({state, setState}) => async (event) => {
    const errors = utils.validateFormBlock(state.steps[1].newLesson, SecondStepSchema);

    if (Object.keys(errors).length > 0) {
      await message.error(Object.values(errors).map(x => <>{x} <br/></>));
      return;
    }


    const step2 = state.steps[1];
    const newId = uuidv4();
    setState({
      ...state, steps: [...state.steps.slice(0, 1), {
        ...step2,
        lessons: {
          ...step2.lessons,
          [newId]: {
            ...step2.newLesson,
            id: newId,
          }
        }
      }, ...state.steps.slice(2)]
    });
  };

type LessonDataRemovedProps = {
  state: WizardStepsState;
  setState: Function;
  id: string;

}
type LessonDataRemoved = (props: LessonDataRemovedProps) => (event: React.MouseEvent<HTMLElement>) => void;
export const lessonDataRemoved: LessonDataRemoved =
  ({state, setState, id}) => (event) => {
    const step2 = state.steps[1];
    if (id) {
      setState({
        ...state, steps: [...state.steps.slice(0, 1), {
          ...step2,
          lessons: {
            ...omit(step2.lessons, id)
          }
        }, ...state.steps.slice(2)]
      })
    }
  };

