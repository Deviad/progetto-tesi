import React, {FC} from "react";
import {Button, Input, Typography} from "antd";
import Text from "antd/es/typography/Text";
import ReactQuill from "react-quill";
import {WizardStepsState} from "../../../WizardSteps";
import {QuestionList} from "../question";
import {quizDeleted, quizDescriptionChanged, quizNameChanged} from "./quizCallbacks";
import {DangerText} from "../../../../common/DangerText";


export const QuizComponent: FC<{ state: WizardStepsState, setState: Function, quizId: string, quizName: string, quizContent: string }> =
    ({state, setState, quizId, quizName, quizContent}) => {
        const [, , step3] = state.steps;

        return (
            <>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Denumire
                    </Text>
                </Typography>
                <Input
                    name="quizName"
                    value={step3.quizzes[quizId].quizName}
                    style={{marginBottom: "0.5rem"}}
                    onChange={quizNameChanged(state, setState, quizId)}
                /> <br />
                <DangerText>{step3.quizzes[quizId].errors.quizName}</DangerText>
                <Typography style={{marginBottom: "0.5rem"}}>
                    <Text style={{fontWeight: "bold"}}>
                        Descriere
                    </Text>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={quizContent}
                            onChange={quizDescriptionChanged(state, setState, quizId)}
                />
                <br/>
                <DangerText>{step3.quizzes[quizId].errors.quizContent}</DangerText>
                <QuestionList state={state} setState={setState} currentQuiz={quizId}/>

                <Button type="primary" danger
                        onClick={quizDeleted(state, setState, quizId)}
                >Sterge</Button>
            </>
        );
    };
