import {WizardStepsState} from "./WizardSteps";
import {QuestionComponent} from "./QuestionComponent";
import React from "react";
import {changeQuestionTitle} from "./questionCallbacks";


export const renderQuestions = (state: WizardStepsState, setState: Function, currentQuiz: string) => {

    const [, , step3] = state.steps;

    return (
        <>
            <>
                {
                    Object.keys(step3?.quizzes[currentQuiz]?.questions || {}).length > 0 &&
                    Object.values(step3.quizzes[currentQuiz].questions).map(q =>
                        <QuestionComponent
                            state={state}
                            setState={setState}
                            id={q.id}
                            quizId={currentQuiz}
                            answers={q.answers}
                            title={q.title}
                            key={q.id}
                            changeTitle={changeQuestionTitle(currentQuiz, q.id, state, setState)}
                        />)
                }
            </>
            {/*<br/>*/}
            {/*<br/>*/}
            {/*<Row style={{display: "flex", flexDirection: "row"}}>*/}

            {/*    <Col span={10} push={1}>*/}
            {/*        <Button type="primary" onClick={() => {*/}
            {/*            console.log("Adauga intrebare")*/}
            {/*        }}> + Adauga intrebare</Button>*/}
            {/*    </Col>*/}
            {/*</Row>*/}
        </>)

}
