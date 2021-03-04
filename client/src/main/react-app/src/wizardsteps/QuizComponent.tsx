import React, {FC} from "react";
import {Button, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import {omit} from "lodash";
import {WizardStepsState} from "./WizardSteps";
import {renderQuestions} from "./renderQuestions";


export const QuizComponent: FC<{ state: WizardStepsState, setState: Function, id: string, quizName: string, quizContent: string }> =
    ({state, setState, id, quizName, quizContent}) => {
        const [, , step3] = state.steps;

        return (
            <>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Denumire
                    </Text>
                </Typography>
                <Input
                    name="quizName"
                    value={step3.quizzes[id].quizName}
                    style={{marginBottom: "0.5rem"}}
                    onChange={(event) => {
                        setState({
                            ...state, steps: [...state.steps.slice(0, 2), {
                                ...step3,
                                quizzes: {
                                    ...step3.quizzes,
                                    [id]: {
                                        ...step3.quizzes[id],
                                        quizName: event.target.value,
                                        modified: true
                                    }
                                }
                            }]
                        })
                    }}
                />
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Descriere
                    </Text>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={quizContent}
                            onChange={(data) => {
                                setState({
                                    ...state, steps: [...state.steps.slice(0, 2), {
                                        ...step3,
                                        quizzes: {
                                            ...step3.quizzes,
                                            [id]: {
                                                ...step3.quizzes[id],
                                                quizContent: data,
                                                modified: true
                                            }
                                        }
                                    }]
                                })
                            }}

                />
                <br/>

                {renderQuestions(state, setState, id)}

                <Button type="primary" danger
                        onClick={(data) => {

                            if (step3.quizzes[id].type === "new") {
                                setState({
                                    ...state, steps: [...state.steps.slice(0, 2), {
                                        ...step3,
                                        quizzes: {
                                            ...omit(step3.quizzes, id)
                                        }
                                    }]
                                })
                            } else {
                                setState({
                                    ...state, steps: [...state.steps.slice(0, 2), {
                                        ...step3,
                                        quizzes: {
                                            ...step3.quizzes,
                                            [id]: {
                                                ...step3.quizzes[id],
                                                deleted: true
                                            }
                                        }
                                    }]
                                })
                            }
                        }}
                >Sterge</Button>
            </>
        );

    };
