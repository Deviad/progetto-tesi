import React, {ChangeEvent, FC} from "react";
import {Col, Input, Radio, Row} from "antd";
import {RadioChangeEvent} from "antd/lib/radio/interface";
import {MinusCircleOutlined, PlusCircleOutlined} from "@ant-design/icons";


export const AnswerComponent: FC<{
    answerTitle: string,
    value: boolean,
    id: string,
    changeValue: (e: RadioChangeEvent) => void,
    changeTitle: (e: ChangeEvent<HTMLInputElement>) => void,
    addAnswer: React.MouseEventHandler<HTMLElement>,
    removeAnswer: React.MouseEventHandler<HTMLElement>,
}> = (props) => {

    return (
        <Row style={{display: "flex", flexDirection: "row", marginBottom: "0.5rem"}} key={props.id}>
            <Col span={5} push={2}>
                <Input name="name" value={props.answerTitle} onChange={props.changeTitle}/>
            </Col>
            <Col span={4} push={3} style={{display: "flex", alignItems: "center"}}>
                <Radio.Group value={props.value} onChange={props.changeValue}>
                    <Radio value={true}>corect</Radio>
                    <Radio value={false}>incorect</Radio>
                </Radio.Group>
            </Col>
            <Col span={4} push={3} style={{display: "flex", alignItems: "center"}}>
                <Col
                    style={{
                        width: "1rem",
                        height: "1rem",
                        display: "flex",
                        justifyContent: "center",
                        alignContent: "center"
                    }}
                    onClick={props.addAnswer}>
                    <PlusCircleOutlined/>
                </Col>
                <Col push={2} style={{
                    width: "1rem",
                    height: "1rem",
                    display: "flex",
                    justifyContent: "center",
                    alignContent: "center"
                }}
                     onClick={props.removeAnswer}>
                    <MinusCircleOutlined/>
                </Col>
            </Col>
        </Row>
    )
};
