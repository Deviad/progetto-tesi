import React, {FC} from "react";
import {QuestionComponent} from ".";
import {WizardStepsState} from "../../wizardsteps/WizardSteps";
import {changeQuestionTitle} from "./questionCallbacks";


export const QuestionList: FC<{ state: WizardStepsState, setState: Function, currentQuiz: string }> =
    ({state, setState, currentQuiz}) => {

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
            </>)

    }
