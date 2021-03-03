import {Button, Collapse, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import React from "react";
import Title from "antd/es/typography/Title";
import {v4 as uuidv4} from 'uuid';
import {omit} from "lodash";
const {Panel} = Collapse;

export const renderQuizzes = (state: any, setState: Function) => {
    if (state.steps[2].quizzes.length == 0) {
        return <div>Nu ai chestionare existente</div>
    } else {
        return Object.entries(state.steps[2].quizzes).map(([k, l]: [string, any]) => (
            <Panel header={l.quizName} key={k}>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Denumire
                    </Text>
                </Typography>
                <Input
                    name="quizName"
                    value={state.steps[2].quizzes[k].quizName}
                    style={{marginBottom: "0.5rem"}}
                    onChange={(event) => {
                        setState({...state, steps: [...state.steps.slice(0, 2), {
                                ...state.steps[2],
                                quizzes: {
                                    ...state.steps[2].quizzes,
                                    [k]: {
                                        ...state.steps[2].quizzes[k],
                                        quizName: event.target.value,
                                        modified: true
                                    }
                                }
                            }]})
                    }}
                />
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Continut
                    </Text>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={l.quizContent}
                            onChange={(data) => {
                                setState({...state, steps: [...state.steps.slice(0, 2), {
                                        ...state.steps[2],
                                        quizzes: {
                                            ...state.steps[2].quizzes,
                                            [k]: {
                                                ...state.steps[2].quizzes[k],
                                                quizContent: data,
                                                modified: true
                                            }
                                        }
                                    }]})}}

                />
                <br/>
                <Button type="primary" danger
                        onClick={(data) => {
                            setState({...state, steps: [...state.steps.slice(0, 2), {
                                    ...state.steps[2],
                                    quizzes: {
                                        ...omit(state.steps[2].quizzes, k)
                                    }
                                }]})}}
                >Sterge</Button>
            </Panel>
        ))
    }
}

export const renderThirdStep = (state: any, setState: Function) => {

    if (state.currentStep === 2) {
        return (
            <>
                <br/>
                <div style={{overflowY: "scroll", height: "40vh"}}>
                    <Typography>
                        <Title level={5}>
                            Adauga un chestionar
                        </Title>
                    </Typography>

                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Denumire
                        </Text>
                    </Typography>
                    <Input name="name" value={state.steps[2].newQuiz.quizName} style={{marginBottom: "0.5rem"}}
                           onChange={(event)=>{
                               setState({...state, steps: [...state.steps.slice(0, 2), {
                                       ...state.steps[2],
                                       newQuiz: {
                                           ...state.steps[2].newQuiz,
                                           quizName: event.target.value,
                                       }
                                   }]});
                           }}
                    />
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Continut
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value={state.steps[2].newQuiz.quizContent}
                                onChange={(data) => {
                                    setState({...state, steps: [...state.steps.slice(0, 2), {
                                            ...state.steps[2],
                                            newQuiz: {
                                                ...state.steps[2].newQuiz,
                                                quizContent: data
                                            },
                                        }]});
                                }}/>
                    <br />
                    <Button type="primary" onClick={()=> {
                        const id = uuidv4();
                        setState({...state, steps: [...state.steps.slice(0, 2), {
                                ...state.steps[2],
                                quizzes: {
                                    ...state.steps[2].quizzes,
                                    [id]: {
                                        id,
                                        quizName: state.steps[2].newQuiz.quizName,
                                        quizContent: state.steps[2].newQuiz.quizContent,
                                        type: state.steps[2].newQuiz.type,
                                        modified: state.steps[2].newQuiz.modified,
                                        deleted: state.steps[2].newQuiz.deleted,
                                    }
                                }
                            }]})
                    }}>Adauga</Button>
                    <br/>
                    <br/>
                    <Typography>
                        <Title level={5}>
                            Chestionare existente
                        </Title>
                    </Typography>
                    <Collapse accordion>
                        {renderQuizzes(state, setState)}
                    </Collapse>
                </div>
                <br/>
            </>)
    }
};
