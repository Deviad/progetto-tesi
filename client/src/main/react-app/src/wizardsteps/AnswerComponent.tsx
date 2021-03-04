import React, {FC, useEffect, useState} from "react";
import {Col, Input, Radio, Row} from "antd";


export const AnswerComponent: FC<{ title: string, value: boolean, id: string }> = (props) => {
    const [state, setState] = useState({title: "", value: false});

    useEffect(() => {
        if (props.title !== null) {
            setState({...state, title: props.title})

        }

        if (props.value !== null) {
            setState({...state, value: props.value})
        }

    }, [props.title, props.value])

    return (
        <Row style={{display: "flex", flexDirection: "row", marginBottom: "0.5rem"}} key={props.id}>
            <Col span={5} push={2}>
                <Input name="name" value={props.title}/>
            </Col>
            <Col span={4} push={3}>
                <Radio.Group value={props.value}>
                    <Radio value={true}>true</Radio>
                    <Radio value={false}>false</Radio>
                </Radio.Group>
            </Col>
        </Row>
    )
};
