import React, {ChangeEvent, FC} from "react";
import {Answer} from "../types";
import {Col, Input, Row, Typography} from "antd";
import {AnswerComponent} from "./AnswerComponent";
import Text from "antd/es/typography/Text";
import {WizardStepsState} from "./WizardSteps";
import {addAnswer, changeAnswerTitle, changeAnswerValue} from "./answerCallbacks";


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
            </Row>
            <br/>
            <Row style={{display: "flex", flexDirection: "row"}}>
                <Col span={10} push={1}>
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
                    removeAnswer={addAnswer(quizId, questionId, state, setState)}
                    id={a.id}/>
            ))}
        </React.Fragment>
    )
};
