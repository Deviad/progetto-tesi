import {Input, Typography} from "antd";
import Title from "antd/es/typography/Title";
import ReactQuill from "react-quill";
import React from "react";

export const onNameChange = (state: any, setState: Function) => (e: any) => {
    state.steps[0].content = {...state.steps[0].content, title: e.target.value}
    setState({
        ...state,
        steps: [...state.steps]
    })
}

export const handleEditorChange = (state: any, setState: Function) => (value: string) => {
    state.steps[0].content = {...state.steps[0].content, description: value}

    setState({
        ...state,
        steps: [...state.steps]
    })
}

export const renderFirstStep = (state: any, setState: Function) => {

    if (state.currentStep === 0) {
        return (
            <>
                <Typography>
                    <Title level={4}>
                        Denumire
                    </Title>
                </Typography>
                <Input name="name" onChange={onNameChange(state, setState)} value={state.steps[0].content.title}/>
                <Typography>
                    <Title level={4}>
                        Descriere
                    </Title>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={state.steps[0].content.description}
                            onChange={handleEditorChange(state, setState)}/>
                <br/>

            </>)
    }
}
