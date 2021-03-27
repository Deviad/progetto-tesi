import {Input, Typography} from "antd";
import Title from "antd/es/typography/Title";
import ReactQuill from "react-quill";
import React, {FC} from "react";
import {WizardStepsState} from "../../WizardSteps";
import {handleEditorChange, onTitleChange} from "./firstStepCallbacks";
import {utils} from "../../../../utils";
import {DangerText} from "../../../common/DangerText";


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
                <Input name="title" onChange={onTitleChange(state, setState)} value={step1.content.title}/>
                {
                    utils.isTrue(step1.content?.errors?.["title"]) &&
                    <DangerText>{step1.content?.errors?.["title"]}</DangerText>
                }
                <Typography>
                    <Title level={4}>
                        Descriere
                    </Title>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={step1.content.description}
                            onChange={handleEditorChange(state, setState)}/>
                <br/>

                {
                    utils.isTrue(step1.content?.errors?.["description"]) &&
                    <DangerText>{step1.content?.errors?.["description"]}</DangerText>
                }

            </>)
    }

    return null;
}
