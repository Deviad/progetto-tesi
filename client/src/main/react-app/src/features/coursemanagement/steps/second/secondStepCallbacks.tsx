import {WizardStepsState} from "../../WizardSteps";
import {ChangeEvent} from "react";

export const lessonNameChange = (
    state: WizardStepsState,
    setState: Function,
    id?: string
) => (event: ChangeEvent<HTMLInputElement>) => {

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
