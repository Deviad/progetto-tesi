import {Button, Collapse, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import React from "react";
import Title from "antd/es/typography/Title";
import {v4 as uuidv4} from 'uuid';
import {omit} from "lodash";
const {Panel} = Collapse;

export const renderLessons = (state: any, setState: Function) => {
    if (state.steps[1].lessons.length == 0) {
        return <div>Nu ai lectile existente</div>
    } else {
        return Object.entries(state.steps[1].lessons).map(([k, l]: [string, any]) => (
            <Panel header={l.lessonName} key={l.id}>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Denumire
                    </Text>
                </Typography>
                <Input
                    name="lessonName"
                    value={state.steps[1].lessons[l.id].lessonName}
                    style={{marginBottom: "0.5rem"}}
                    onChange={(event) => {
                        setState({...state, steps: [...state.steps.slice(0, 1), {
                                ...state.steps[1],
                                lessons: {
                                    ...state.steps[1].lessons,
                                    [l.id]: {
                                        ...state.steps[1].lessons[l.id],
                                        lessonName: event.target.value,
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
                <ReactQuill style={{background: "#fff"}} value={l.lessonContent}
                            onChange={(data) => {
                                setState({...state, steps: [...state.steps.slice(0, 1), {
                                        ...state.steps[1],
                                        lessons: {
                                            ...state.steps[1].lessons,
                                            [l.id]: {
                                                ...state.steps[1].lessons[l.id],
                                                lessonContent: data,
                                                modified: true
                                            }
                                        }
                                    }]})}}

                />
                <br/>
                <Button type="primary" danger
                        onClick={(data) => {
                            setState({...state, steps: [...state.steps.slice(0, 1), {
                                    ...state.steps[1],
                                    lessons: {
                                      ...omit(state.steps[1].lessons, l.id)
                                    }
                                }]})}}
                >Sterge</Button>
            </Panel>
        ))
    }
}

export const renderSecondStep = (state: any, setState: Function) => {

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
                    <Input name="name" value={state.steps[1].newLesson.lessonName} style={{marginBottom: "0.5rem"}}
                           onChange={(event)=>{
                               setState({...state, steps: [...state.steps.slice(0, 1), {
                                       ...state.steps[1],
                                       newLesson: {
                                           ...state.steps[1].newLesson,
                                           lessonName: event.target.value,
                                       }
                                   }]});
                           }}
                    />
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Continut
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value={state.steps[1].newLesson.lessonContent}
                                onChange={(data)=>{
                                    setState({...state, steps: [...state.steps.slice(0, 1), {
                                            ...state.steps[1],
                                             newLesson: {
                                                 ...state.steps[1].newLesson,
                                                 lessonContent: data
                                             },
                                        }]});
                                }}/>
                    <br />
                    <Button type="primary" onClick={()=> {
                        const id = uuidv4();
                        setState({...state, steps: [...state.steps.slice(0, 1), {
                                ...state.steps[1],
                                lessons: {
                                    ...state.steps[1].lessons,
                                    [id]: {
                                        id,
                                        lessonName: state.steps[1].newLesson.lessonName,
                                        lessonContent: state.steps[1].newLesson.lessonContent,
                                        type: state.steps[1].newLesson.type,
                                        modified: state.steps[1].newLesson.modified,
                                        deleted: state.steps[1].newLesson.deleted,
                                    }
                                }
                            }]})
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
};
