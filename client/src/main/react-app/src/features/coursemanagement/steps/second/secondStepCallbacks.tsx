import {WizardStepsState} from "../../WizardSteps";
import React, {ChangeEvent} from "react";
import {v4 as uuidv4} from "uuid";
import {cloneDeep, omit} from "lodash";
import {utils} from "../../../../utils";
import {object, string} from "yup";
import {FormError} from "../../../../types";
import {message} from "antd";

type LessonNameChangedProps = {
    state: WizardStepsState;
    setState: Function;
    id?: string;
};
export const SecondStepSchema = object().shape({
    lessonName: string().required().trim().min(3).max(100).test('is-blank', '${path} nu poate fi gol', (value,)=> value !== ''),
    lessonContent: string().required().trim().min(3).test('is-blank', '${path} nu poate fi gol', (value,)=> value !== ''),
}).required();
type LessonNameChanged = (props: LessonNameChangedProps) => (event: ChangeEvent<HTMLInputElement>) => void;


function validateFormInput<T extends unknown>(objectToValidate: { [key: string]: any, errors?: FormError }, value: T, path: string) {
    const copy = cloneDeep(objectToValidate);

    if ((value as React.ChangeEvent<HTMLInputElement>).target && typeof (value as React.ChangeEvent<HTMLInputElement>).target.value !== undefined) {
        copy[path] = (value as React.ChangeEvent<HTMLInputElement>).target.value
    } else {
        copy[path] = value;
    }

    let errorsMap;

    try {
        errorsMap = utils.validateBySchema(copy, SecondStepSchema, path);
    } catch (error) {
        console.log(error);
        message.error(error.message);
        return;
    }
    if (Object.keys(errorsMap).length === 0) {

        if (objectToValidate.errors && objectToValidate.errors[path]) {

            //Daca acum user-ul a introdus un sir fara erori atunci scoatem eroarile din stare lui step2.

            objectToValidate.errors = omit(objectToValidate.errors, path);
        }

    } else {

        if (!objectToValidate.errors) {
            objectToValidate.errors = {};
        }

        objectToValidate.errors = {...objectToValidate.errors, [path]: errorsMap[path]};
    }
}

export const lessonNameChangeChanged: LessonNameChanged =
    ({state, setState, id}) => (event) => {

        const step2 = state.steps[1];


        if (id) {
            const currentLesson = step2.lessons[id];
            validateFormInput(currentLesson, event, "lessonName");

            setState({
                ...state, steps: [...state.steps.slice(0, 1), {
                    ...step2,
                    lessons: {
                        ...step2.lessons,
                        [id]: {
                            ...step2.lessons[id],
                            lessonName: event.target.value,
                            modified: true
                        }
                    },
                }, ...state.steps.slice(2)]
            })
        } else {
            validateFormInput(step2.newLesson, event, "lessonName");
            setState({
                ...state, steps: [...state.steps.slice(0, 1), {
                    ...step2,
                    newLesson: {
                        ...step2.newLesson,
                        lessonName: event.target.value,
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

            validateFormInput(currentLesson, utils.stripHtmlTags(data), "lessonContent");

            setState({
                ...state, steps: [...state.steps.slice(0, 1), {
                    ...step2,
                    lessons: {
                        ...step2.lessons,
                        [id]: {
                            ...step2.lessons[id],
                            lessonContent: data,
                            modified: true
                        }
                    }
                }, ...state.steps.slice(2)]
            })
        } else {

            validateFormInput(step2.newLesson, utils.stripHtmlTags(data), "lessonContent");

            setState({
                ...state, steps: [...state.steps.slice(0, 1), {
                    ...step2,
                    newLesson: {
                        ...step2.newLesson,
                        lessonContent: data
                    },
                }, ...state.steps.slice(2)]
            });
        }
    }


type LessonDataAddedProps = {
    state: WizardStepsState;
    setState: Function;
}

type LessonDataAdded = (props: LessonDataAddedProps) => (event: React.MouseEvent<HTMLElement>) => void;

export const lessonDataAdded: LessonDataAdded =
    ({state, setState}) => (event) => {
        const step2 = state.steps[1];
        const newId = uuidv4();
        setState({
            ...state, steps: [...state.steps.slice(0, 1), {
                ...step2,
                lessons: {
                    ...step2.lessons,
                    [newId]: {
                        id: newId,
                        lessonName: step2.newLesson.lessonName,
                        lessonContent: step2.newLesson.lessonContent,
                        type: step2.newLesson.type,
                        modified: step2.newLesson.modified,
                        deleted: step2.newLesson.deleted,
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

