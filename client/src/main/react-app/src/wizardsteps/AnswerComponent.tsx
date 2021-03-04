import React, {ChangeEvent, FC} from "react";
import {Col, Input, Radio, Row} from "antd";
import {RadioChangeEvent} from "antd/lib/radio/interface";


export const AnswerComponent: FC<{
    answerTitle: string,
    value: boolean,
    id: string,
    changeValue: (e: RadioChangeEvent) => void,
    changeTitle: (e: ChangeEvent<HTMLInputElement>) => void,
}> = (props) => {

    return (
        <Row style={{display: "flex", flexDirection: "row", marginBottom: "0.5rem"}} key={props.id}>
            <Col span={5} push={2}>
                <Input name="name" value={props.answerTitle} onChange={props.changeTitle} />
            </Col>
            <Col span={4} push={3}>
                <Radio.Group value={props.value} onChange={props.changeValue}>
                    <Radio value={true}>true</Radio>
                    <Radio value={false}>false</Radio>
                </Radio.Group>
            </Col>
        </Row>
    )
};
