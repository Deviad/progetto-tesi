import React, {ChangeEvent, FC} from "react";
import {Col, Input, Row, Typography} from "antd";
import Text from "antd/es/typography/Text";
import {MinusCircleOutlined, PlusCircleOutlined} from "@ant-design/icons";
import {addQuestion, removedQuestion} from "./questionCallbacks";
import {WizardStepsState} from "../../../WizardSteps";
import {Answer} from "../../../../../types";
import {addAnswer, changeAnswerTitle, changeAnswerValue, removeAnswer} from "../../../answerCallbacks";
import {AnswerComponent} from "../answer";


export const QuestionComponent: FC<{
    state: WizardStepsState, setState: Function,
    answers: Record<string, Answer>,
    title: string,
    id: string
    quizId: string,
    changeTitle: (e: ChangeEvent<HTMLInputElement>) => void,
}> = (props) => {

    const {state, setState, answers, title, id: questionId, quizId, changeTitle} = props;

    return (
        <React.Fragment key={questionId}>
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
                <Col span={10} push={1}> <Input name="name" value={title} onChange={changeTitle}/></Col>
                <Col span={4} push={2} style={{display: "flex", alignItems: "center"}}>
                    <Col
                        style={{
                            width: "1rem",
                            height: "1rem",
                            display: "flex",
                            justifyContent: "center",
                            alignContent: "center"
                        }}
                        onClick={addQuestion(quizId, questionId, state, setState)}>
                        <PlusCircleOutlined/>
                    </Col>
                    <Col push={2} style={{
                        width: "1rem",
                        height: "1rem",
                        display: "flex",
                        justifyContent: "center",
                        alignContent: "center"
                    }}
                         onClick={removedQuestion(quizId, questionId, state, setState)}>
                        <MinusCircleOutlined/>
                    </Col>
                </Col>
            </Row>
            <br/>
            <Row style={{display: "flex", flexDirection: "row"}}>
                <Col span={10} push={2}>
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Raspunsuri
                        </Text>
                    </Typography>
                </Col>
            </Row>
            {Object.keys(answers).length > 0 && Object.values(props.answers).map(a => (
                <AnswerComponent
                    value={a.value} answerTitle={a.title}
                    changeTitle={changeAnswerTitle(quizId, questionId, a.id, state, setState)}
                    changeValue={changeAnswerValue(quizId, questionId, a.id, state, setState)}
                    addAnswer={addAnswer(quizId, questionId, state, setState)}
                    removeAnswer={removeAnswer(quizId, questionId, a.id, state, setState)}
                    key={a.id}
                    id={a.id}/>
            ))}
        </React.Fragment>
    )
};
