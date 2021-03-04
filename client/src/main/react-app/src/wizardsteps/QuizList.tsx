import {WizardStepsState} from "./WizardSteps";
import {Collapse} from "antd";
import {PanelWrapper} from "./PanelWrapper";
import {QuizComponent} from "./QuizComponent";
import React, {FC, useEffect} from "react";
import {useState} from "reinspect";


export const QuizList: FC<{

    state: WizardStepsState,
    setState: Function,
    keys: string[],

}> = ({state, setState, keys}) => {

    const [, , step3] = state.steps;

    const [activeKey, setActiveKey] = useState("", 'activeKey');

    useEffect(()=>{
       setActiveKey(keys[keys.length - 1])
    }, [keys]);


    if (keys.length == 0) {
        return <div>Nu ai chestionare existente</div>
    } else {
        return <Collapse activeKey={activeKey} accordion>{
            Object.entries(step3.quizzes)
                .filter(([k, q]) => {
                    if (!q.deleted) {
                        return [k, q];
                    }
                })
                .map(([k, l], index) => {
                    return (
                        <PanelWrapper header={l.quizName} id={k} key={k} activeKey={activeKey} setActiveKey={setActiveKey}>
                            <QuizComponent
                                id={l.id}
                                quizContent={l.quizContent}
                                quizName={l.quizName}
                                state={state}
                                setState={setState}/>
                        </PanelWrapper>
                    )
                })
        }</Collapse>
    }
}
