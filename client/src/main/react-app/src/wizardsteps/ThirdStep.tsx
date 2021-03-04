import {Button, Col, Input, Radio, Row, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import React, {FC, useEffect, useState} from "react";
import Title from "antd/es/typography/Title";
import {v4 as uuidv4} from 'uuid';
import {WizardStepsState} from "./WizardSteps";
import {Answer} from "../types";
import {useState as reuUseState} from "reinspect";
import {renderQuizzes} from "./renderQuizzes";


const AnswerComponent: FC<{ title: string, value: boolean, id: string }> = (props) => {
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


const QuestionComponent: FC<{ answers: Record<string, Answer>, title: string }> = (props) => {
    return (
        <>
            <Row style={{display: "flex", flexDirection: "row"}}>
                <Col span={10} push={1}> <Input name="name" value={props.title}/></Col>
            </Row>
            {Object.keys(props.answers).length > 0 && Object.values(props.answers).map(a => (
                <AnswerComponent title={a.title} value={a.correct} id={a.id} key={a.id}/>
            ))}
        </>
    )
};

const renderQuestions = (state: WizardStepsState, setState: Function, currentQuiz: string) => {

    const [, , step3] = state.steps;

    return (
        <>
            <>
                {
                    Object.keys(step3?.quizzes[currentQuiz]?.questions).length > 0 &&
                    Object.values(step3.quizzes[currentQuiz].questions).map(q =>
                        <QuestionComponent answers={q.answers} title={q.title} key={q.id}/>)
                }
            </>
            <br/>
            <br/>
            <Row style={{display: "flex", flexDirection: "row"}}>

                <Col span={10} push={1}>
                    <Button type="primary" onClick={() => {
                        console.log("Adauga intrebare")
                    }}> + Adauga intrebare</Button>
                </Col>
            </Row>
        </>)

}


export const ThirdStep: FC<{ state: WizardStepsState, setState: Function }> = ({state, setState}) => {

    const [newQuizState, setNewQuizState] = reuUseState({
        quizName: "",
        quizContent: "",
        type: "new",
        modified: false,
        deleted: false
    }, 'quiz-state')
    const {quizName, quizContent, type, modified, deleted} = newQuizState;

    if (state.currentStep === 2) {
        return (
            <>
                <br/>
                <div style={{overflowY: "scroll", height: "40vh"}}>
                    <Typography>
                        <Title level={5}>
                            Adauga un chestionar
                        </Title>
                    </Typography>

                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Denumire
                        </Text>
                    </Typography>
                    <Input name="name" value={quizName} style={{marginBottom: "0.5rem"}}
                           onChange={(event) => {
                               setNewQuizState({...newQuizState, quizName: event.target.value});
                           }}
                    />
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Descriere
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value={quizContent}
                                onChange={(data) => {
                                    setNewQuizState({...newQuizState, quizContent: data});
                                }}/>
                    <br/>
                    <br/>
                    {/*<Row style={{display: "flex", flexDirection: "row"}}>*/}
                    {/*    <Col span={10} push={1}>*/}
                    {/*        <Input  />*/}
                    {/*    </Col>*/}
                    {/*    <Col span={4} push={2}>*/}
                    {/*        <Radio.Group>*/}
                    {/*            <Radio value={true}>true</Radio>*/}
                    {/*            <Radio value={false}>false</Radio>*/}
                    {/*        </Radio.Group>*/}
                    {/*    </Col>*/}
                    {/*</Row>*/}
                    <br/>
                    <Button type="primary" onClick={() => {
                        const id = uuidv4();
                        setState({
                            ...state, steps: [...state.steps.slice(0, 2), {
                                ...state.steps[2],
                                quizzes: {
                                    ...state.steps[2].quizzes,
                                    [id]: {
                                        id,
                                        quizName: quizName,
                                        quizContent: quizContent,
                                        type: type,
                                        modified: modified,
                                        deleted: deleted,
                                    }
                                }
                            }]
                        })
                    }}>Adauga chestionar</Button>
                    <br/>
                    <br/>
                    <Typography>
                        <Title level={5}>
                            Chestionare existente
                        </Title>
                    </Typography>

                    {renderQuizzes(state, setState)}
                </div>
                <br/>
            </>)
    }
    return null;
};
