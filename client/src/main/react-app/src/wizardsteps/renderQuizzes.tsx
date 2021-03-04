import {WizardStepsState} from "./WizardSteps";
import {Collapse} from "antd";
import {QuizComponent} from "./QuizComponent";
import React from "react";

const {Panel} = Collapse;

export const renderQuizzes = (state: WizardStepsState, setState: Function) => {

    const [, , step3] = state.steps;

    if (Object.keys(step3.quizzes).length == 0) {
        return <div>Nu ai chestionare existente</div>
    } else {
        return <Collapse accordion>{
            Object.entries(step3.quizzes)
                .filter(([k, q]) => {
                    if (!q.deleted) {
                        return [k, q];
                    }
                }).map(([k, l]) => (
                <Panel header={l.quizName} key={k}>
                    <QuizComponent
                        id={l.id}
                        quizContent={l.quizContent}
                        quizName={l.quizName}
                        state={state}
                        setState={setState}/>
                </Panel>
            ))
        }</Collapse>
    }
}
