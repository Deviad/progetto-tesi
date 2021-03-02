import {Collapse, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import {noop} from "lodash";
import React from "react";
import Title from "antd/es/typography/Title";
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
                        state.steps[1].lessons[l.id].lessonName = event.target.value;
                        state.steps[1].lessons[l.id].modified = true;
                        state.steps[1].lessons = {...state.steps[1].lessons, [l.id]: state.steps[1].lessons[l.id]}
                        setState({...state, steps: [...state.steps]})
                    }}/>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Continut
                    </Text>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={l.lessonContent}
                            onChange={noop}/>
                <br/>
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
                    <Input name="name" value="" style={{marginBottom: "0.5rem"}}/>
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Continut
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value=""
                                onChange={noop}/>
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
