import {Button, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import React, {FC} from "react";
import Title from "antd/es/typography/Title";
import {v4 as uuidv4} from 'uuid';
import {WizardStepsState} from "./WizardSteps";
import {useState as reuUseState} from "reinspect";
import {renderQuizzes} from "./renderQuizzes";
import {produce} from 'immer';
import {Quiz} from "../types";

export const ThirdStep: FC<{ state: WizardStepsState, setState: Function }> = ({state, setState}) => {

    const [newQuizState, setNewQuizState] = reuUseState<Quiz>({
        id: "",
        quizName: "",
        quizContent: "",
        type: "new",
        modified: false,
        deleted: false,
        questions: {}
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
                        setState(produce((draft: WizardStepsState) => {
                            draft
                                .steps[2]
                                .quizzes[id] = {
                                    questions: {},
                                    id,
                                    quizName: quizName,
                                    quizContent: quizContent,
                                    type: type,
                                    modified: modified,
                                    deleted: deleted
                            }
                        }));

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
