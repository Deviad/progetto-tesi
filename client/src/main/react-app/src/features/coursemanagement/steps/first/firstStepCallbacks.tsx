import {WizardStepsState} from "../../WizardSteps";
import {utils} from "../../../../utils";
import {object, string} from "yup";
import {ChangeEvent} from "react";

import "../../../../utils/yupConfig"

export const FirstStepSchema = object().shape({
    // eslint-disable-next-line no-template-curly-in-string
    description: string().required().trim().min(3).test('is-blank', '${path} nu poate fi gol', (value,)=> value !== ''),
    // eslint-disable-next-line no-template-curly-in-string
    title: string().required().trim().min(3).max(100).test('is-blank', '${path} nu poate fi gol', (value,)=> value !== ''),
}).required();

export const onTitleChange = (state: WizardStepsState, setState: Function) => (e: ChangeEvent<HTMLInputElement>) => {

    const [step1] = state.steps;

    step1.content.errors = utils.validateFormInput({
        objectToValidate: step1.content,
        schema: FirstStepSchema,
        value: e,
        path: "title"
    });

    step1.content = {...step1.content, title: e.target.value, modified: true}

    setState({
        ...state,
        steps: [...state.steps]
    })

}

export const handleEditorChange = (state: any, setState: Function) => (value: string) => {
    const [step1] = state.steps;

    step1.content.errors = utils.validateFormInput({
        objectToValidate: step1.content,
        value,
        schema: FirstStepSchema,
        path: "description"});

    step1.content = {...step1.content, description: value, modified: true}

    setState({
        ...state,
        steps: [...state.steps]
    })

}
