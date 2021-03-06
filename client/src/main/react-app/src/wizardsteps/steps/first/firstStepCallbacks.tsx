import {WizardStepsState} from "../../WizardSteps";

export const onNameChange = (state: WizardStepsState, setState: Function) => (e: any) => {

    const [step1] = state.steps;

    step1.content = {...step1.content, title: e.target.value}
    setState({
        ...state,
        steps: [...state.steps]
    })
}

export const handleEditorChange = (state: any, setState: Function) => (value: string) => {
    const [step1] = state.steps;
    step1.content = {...step1.content, description: value}

    setState({
        ...state,
        steps: [...state.steps]
    })
}
