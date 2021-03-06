import {Button} from "antd";
import React, {FC} from "react";
import {v4 as uuidv4} from 'uuid';
import {WizardStepsState} from "../../WizardSteps";
import {produce} from 'immer';
import {QuizList} from "./quiz";

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
                    <Button type="primary" onClick={() => {
                        const quizId = uuidv4();
                        const questionId = uuidv4();
                        const answerId = uuidv4();

                        setState(produce((draft: WizardStepsState) => {
                            draft
                                .steps[2]
                                .quizzes[quizId] = {
                                id: quizId,
                                questions: {
                                    [questionId]: {
                                        modified: false,
                                        deleted: false,
                                        id: questionId,
                                        title: "Completeaza",
                                        answers: {
                                            [answerId]: {
                                                value: true,
                                                id: answerId,
                                                title: "Completeaza",
                                                modified: false,
                                                deleted: false,
                                            }
                                        }
                                    }
                                },
                                quizName: "Introduci o denumire",
                                quizContent: "Introduci o descriere",
                                type: "new",
                                modified: false,
                                deleted: false,
                            }
                        }));
                    }}>Adauga chestionar</Button>
                    <br/>
                    <br/>
                    <QuizList state={state} setState={setState} keys={keys}/>
                </div>
                <br/>
            </>)
    }
    return null;
};
