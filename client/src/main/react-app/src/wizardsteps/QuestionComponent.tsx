import React, {FC} from "react";
import {Answer} from "../types";
import {Col, Input, Row, Typography} from "antd";
import {AnswerComponent} from "./AnswerComponent";
import Text from "antd/es/typography/Text";


export const QuestionComponent: FC<{ answers: Record<string, Answer>, title: string }> = (props) => {
    return (
        <>
            <Row style={{display: "flex", flexDirection: "row"}}>
                <Col span={10} push={1}>
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                           Intrebare
                        </Text>
                    </Typography>
                </Col>
            </Row>
            <Row style={{display: "flex", flexDirection: "row"}}>
                <Col span={10} push={1}> <Input name="name" value={props.title}/></Col>
            </Row>
            <br />
            <Row style={{display: "flex", flexDirection: "row"}}>
                <Col span={10} push={1}>
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                           Raspunsuri
                        </Text>
                    </Typography>
                </Col>
            </Row>
            {Object.keys(props.answers).length > 0 && Object.values(props.answers).map(a => (
                <AnswerComponent title={a.title} value={a.correct} id={a.id} key={a.id}/>
            ))}
        </>
    )
};
