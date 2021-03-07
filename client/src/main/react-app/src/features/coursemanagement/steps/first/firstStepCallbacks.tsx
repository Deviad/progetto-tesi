import {WizardStepsState} from "../../WizardSteps";
import {utils} from "../../../../utils";
import {object, string} from "yup";
import {cloneDeep, omit} from 'lodash';
import {ChangeEvent} from "react";

export const FirstStepSchema = object().shape({
    description: string().required().min(3),
    title: string().required().min(3),
}).required();


export const onTitleChange = (state: WizardStepsState, setState: Function) => (e: ChangeEvent<HTMLInputElement>) => {

    const [step1] = state.steps;

    step1.content = {...step1.content, title: e.target.value}

    const copy = cloneDeep(step1.content);
    copy.title = e.target.value;

    const errorsMap = utils.validateBySchema(copy, FirstStepSchema, "title");


    if (Object.keys(errorsMap).length === 0) {

        //Daca acum user-ul a introdus un sir fara erori atunci scoatem eroarile din stare lui step1.

        if (step1.errors["title"]) {
            step1.errors = omit(step1.errors, "title");
        }

        setState({
            ...state,
            steps: [...state.steps]
        })

    } else {
        step1.errors = {...step1.errors, title: errorsMap["title"]};
        setState({
            ...state,
            steps: [...state.steps]
        })
    }

}

export const handleEditorChange = (state: any, setState: Function) => (value: string) => {
    const [step1] = state.steps;
    step1.content = {...step1.content, description: value}

    const copy = cloneDeep(step1.content);


    copy.description = utils.stripHtmlTags(value);

    const errorsMap = utils.validateBySchema(copy, FirstStepSchema, "description");

    if (Object.keys(errorsMap).length === 0) {

        if (step1.errors["description"]) {
            step1.errors = omit(step1.errors, "description");
        }

        setState({
            ...state,
            steps: [...state.steps]
        })
    } else {
        step1.errors = {...step1.errors, description: errorsMap["description"]};
        setState({
            ...state,
            steps: [...state.steps]
        })
    }
}
