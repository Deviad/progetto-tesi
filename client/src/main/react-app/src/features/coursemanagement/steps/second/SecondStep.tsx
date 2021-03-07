import {Button, Collapse, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import React from "react";
import Title from "antd/es/typography/Title";
import {v4 as uuidv4} from 'uuid';
import {omit} from "lodash";
import {WizardStepsState} from "../../WizardSteps";
import {lessonDataAdded, lessonContentChanged, lessonNameChangeChanged, lessonDataRemoved} from "./secondStepCallbacks";

const {Panel} = Collapse;

export const renderLessons = (state: WizardStepsState, setState: Function) => {
    const [, step2,] = state.steps;

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
                    onChange={lessonNameChangeChanged({state, setState, id: k})}
                />
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Continut
                    </Text>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={l.lessonContent}
                            onChange={lessonContentChanged({state, setState, id: k})}
                />
                <br/>
                <Button type="primary" danger
                        onClick={lessonDataRemoved({state, setState, id: k})}
                >Sterge</Button>
            </Panel>
        ))
    }
}

export const SecondStep = ({state, setState}: { state: WizardStepsState, setState: Function }) => {
    const [, step2,] = state.steps;
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
                           onChange={lessonNameChangeChanged({state, setState})}
                    />
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Continut
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value={step2.newLesson.lessonContent}
                                onChange={lessonContentChanged({state, setState})}/>
                    <br/>
                    <Button type="primary" onClick={lessonDataAdded({state, setState})}>Adauga</Button>
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
