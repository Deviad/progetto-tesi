import {WizardStepsState} from "../../WizardSteps";
import {utils} from "../../../../utils";
import {object, string} from "yup";
import {ChangeEvent} from "react";

export const FirstStepSchema = object().shape({
    description: string().required().trim().min(3).test('is-blank', '${path} nu poate fi gol', (value,)=> value !== ''),
    title: string().required().trim().min(3).max(100).test('is-blank', '${path} nu poate fi gol', (value,)=> value !== ''),
}).required();

export const onTitleChange = (state: WizardStepsState, setState: Function) => (e: ChangeEvent<HTMLInputElement>) => {

    const [step1] = state.steps;


    utils.validateFormInput({
        objectToValidate: step1.content,
        schema: FirstStepSchema,
        value: e,
        path: "title"});

    step1.content = {...step1.content, title: e.target.value}

    setState({
        ...state,
        steps: [...state.steps]
    })

}

export const handleEditorChange = (state: any, setState: Function) => (value: string) => {
    const [step1] = state.steps;

    utils.validateFormInput({
        objectToValidate: step1.content,
        value,
        schema: FirstStepSchema,
        path: "description"});

    step1.content = {...step1.content, description: value}

    setState({
        ...state,
        steps: [...state.steps]
    })

}
