import {Button} from "antd";
import React, {FC} from "react";
import {WizardStepsState} from "../../WizardSteps";
import {QuizList} from "./quiz";
import {newDefaultQuizAdded} from "./thirdStepCallbacks";

export const ThirdStep: FC<{ state: WizardStepsState, setState: Function }> = ({state, setState}) => {

    const [, , step3] = state.steps;
    const keys = Object.keys(step3.quizzes);

    if (state.currentStep === 2) {
        return (
            <>
                <br/>
                <div style={{overflowY: "scroll", height: "40vh"}}>
                    <br/>
                    <br/>
                    <Button type="primary" onClick={newDefaultQuizAdded(setState)}>Adauga chestionar</Button>
                    <br/>
                    <br/>
                    <QuizList state={state} setState={setState} keys={keys}/>
                </div>
                <br/>
            </>)
    }
    return null;
};
