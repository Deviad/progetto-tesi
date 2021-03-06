import {Input, Typography} from "antd";
import Title from "antd/es/typography/Title";
import ReactQuill from "react-quill";
import React, {FC} from "react";
import {WizardStepsState} from "./WizardSteps";

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

export const FirstStep: FC<{ state: WizardStepsState, setState: Function }> = ({state, setState}) => {
    const [step1] = state.steps;
    if (state.currentStep === 0) {
        return (
            <>
                <Typography>
                    <Title level={4}>
                        Denumire
                    </Title>
                </Typography>
                <Input name="name" onChange={onNameChange(state, setState)} value={step1.content.title}/>
                <Typography>
                    <Title level={4}>
                        Descriere
                    </Title>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={step1.content.description}
                            onChange={handleEditorChange(state, setState)}/>
                <br/>

            </>)
    }

    return null;
}
