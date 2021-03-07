import {WizardStepsState} from "../../WizardSteps";
import React, {ChangeEvent} from "react";
import {v4 as uuidv4} from "uuid";
import {omit} from "lodash";

type LessonNameChangedProps = {
    state: WizardStepsState;
    setState: Function;
    id?: string;
};

type LessonNameChanged = (props: LessonNameChangedProps) => (event: ChangeEvent<HTMLInputElement>) => void;

export const lessonNameChangeChanged: LessonNameChanged =
    ({state, setState, id}) => (event) => {

        const step2 = state.steps[1];

        if (id) {
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
                    }
                }, ...state.steps.slice(2)]
            })
        } else {
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
