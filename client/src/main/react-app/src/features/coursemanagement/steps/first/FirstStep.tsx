import {Input, Typography} from "antd";
import Title from "antd/es/typography/Title";
import ReactQuill from "react-quill";
import React, {FC} from "react";
import {WizardStepsState} from "../../WizardSteps";
import {handleEditorChange, onNameChange} from "./firstStepCallbacks";


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
