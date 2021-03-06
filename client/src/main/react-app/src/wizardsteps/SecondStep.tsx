import {Button, Collapse, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import React from "react";
import Title from "antd/es/typography/Title";
import {v4 as uuidv4} from 'uuid';
import {omit} from "lodash";
import {WizardStepsState} from "./WizardSteps";
const {Panel} = Collapse;

export const renderLessons = (state: WizardStepsState, setState: Function) => {
    const [,step2,] = state.steps;

    if (Object.keys(step2.lessons).length == 0) {
        return <div>Nu ai lectile existente</div>
    } else {
        return Object.entries(step2.lessons).map(([k, l]: [string, any]) => (
            <Panel header={l.lessonName} key={k}>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Denumire
                    </Text>
                </Typography>
                <Input
                    name="lessonName"
                    value={step2.lessons[k].lessonName}
                    style={{marginBottom: "0.5rem"}}
                    onChange={(event) => {
                        setState({...state, steps: [...state.steps.slice(0, 1), {
                                ...step2,
                                lessons: {
                                    ...step2.lessons,
                                    [k]: {
                                        ...step2.lessons[k],
                                        lessonName: event.target.value,
                                        modified: true
                                    }
                                }
                            }, ...state.steps.slice(2)]})
                    }}
                />
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Continut
                    </Text>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={l.lessonContent}
                            onChange={(data) => {
                                setState({...state, steps: [...state.steps.slice(0, 1), {
                                        ...step2,
                                        lessons: {
                                            ...step2.lessons,
                                            [k]: {
                                                ...step2.lessons[k],
                                                lessonContent: data,
                                                modified: true
                                            }
                                        }
                                    }, ...state.steps.slice(2)]})}}

                />
                <br/>
                <Button type="primary" danger
                        onClick={(data) => {
                            setState({...state, steps: [...state.steps.slice(0, 1), {
                                    ...step2,
                                    lessons: {
                                      ...omit(step2.lessons, k)
                                    }
                                }, ...state.steps.slice(2)]})}}
                >Sterge</Button>
            </Panel>
        ))
    }
}

export const SecondStep = ({state, setState}: {state: WizardStepsState, setState: Function}) => {
    const [,step2,] = state.steps;
    if (state.currentStep === 1) {
        return (
            <>
                <br/>
                <div style={{overflowY: "scroll", height: "40vh"}}>
                    <Typography>
                        <Title level={5}>
                            Adauga o lectie
                        </Title>
                    </Typography>

                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Denumire
                        </Text>
                    </Typography>
                    <Input name="name" value={step2.newLesson.lessonName} style={{marginBottom: "0.5rem"}}
                           onChange={(event)=>{
                               setState({...state, steps: [...state.steps.slice(0, 1), {
                                       ...step2,
                                       newLesson: {
                                           ...step2.newLesson,
                                           lessonName: event.target.value,
                                       }
                                   }, ...state.steps.slice(2)]});
                           }}
                    />
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Continut
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value={step2.newLesson.lessonContent}
                                onChange={(data)=>{
                                    setState({...state, steps: [...state.steps.slice(0, 1), {
                                            ...step2,
                                             newLesson: {
                                                 ...step2.newLesson,
                                                 lessonContent: data
                                             },
                                        }, ...state.steps.slice(2)]});
                                }}/>
                    <br />
                    <Button type="primary" onClick={()=> {
                        const id = uuidv4();
                        setState({...state, steps: [...state.steps.slice(0, 1), {
                                ...step2,
                                lessons: {
                                    ...step2.lessons,
                                    [id]: {
                                        id,
                                        lessonName: step2.newLesson.lessonName,
                                        lessonContent: step2.newLesson.lessonContent,
                                        type: step2.newLesson.type,
                                        modified: step2.newLesson.modified,
                                        deleted: step2.newLesson.deleted,
                                    }
                                }
                            }, ...state.steps.slice(2)]})
                    }}>Adauga</Button>
                    <br/>
                    <br/>
                    <Typography>
                        <Title level={5}>
                            Lectile existente
                        </Title>
                    </Typography>
                    <Collapse accordion>
                        {renderLessons(state, setState)}
                    </Collapse>
                </div>
                <br/>
            </>)
    }
    return null;
};
