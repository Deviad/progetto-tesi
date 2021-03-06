import {Collapse} from "antd";
import React, {FC, useEffect} from "react";
import {useState} from "reinspect";
import {PanelWrapper} from "../../../PanelWrapper";
import {WizardStepsState} from "../../../WizardSteps";
import {QuizComponent} from "./index";


export const QuizList: FC<{

    state: WizardStepsState,
    setState: Function,
    keys: string[],

}> = ({state, setState, keys}) => {

    const [, , step3] = state.steps;

    const [activeKey, setActiveKey] = useState("", 'activeKey');

    useEffect(() => {
        setActiveKey(keys[keys.length - 1])
    }, [keys.length]);


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
                .map(([k, quiz], index) => {
                    return (
                        <PanelWrapper header={quiz.quizName} id={k} key={k} activeKey={activeKey}
                                      setActiveKey={setActiveKey}>
                            <QuizComponent
                                quizId={quiz.id}
                                quizContent={quiz.quizContent}
                                quizName={quiz.quizName}
                                state={state}
                                setState={setState}/>
                        </PanelWrapper>
                    )
                })
        }</Collapse>
    }
}
