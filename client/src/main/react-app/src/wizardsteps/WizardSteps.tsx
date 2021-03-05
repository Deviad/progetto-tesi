import {Button, message, Modal, Steps} from 'antd';
import {useState} from "reinspect";
import React, {useEffect} from "react";
import {Lesson, Quiz} from '../types';
import {renderFirstStep} from "./renderFirstStep";
import {renderSecondStep} from "./renderSecondStep";
import {ThirdStep} from './ThirdStep';

export interface StepContent {

}

export interface StepContent1 extends StepContent {
    title: string,
    content: {
        id: string,
        title: string,
        description: string
    },
}

export interface StepContent2 extends StepContent {
    title: string,
    newLesson: {
        id: string,
        lessonName: string,
        lessonContent: string,
        type: "new",
        deleted: boolean,
        modified: boolean,
    }
    lessons: Record<string, Lesson>
}

export interface StepContent3 extends StepContent {
    title: string,
    quizzes: Record<string, Quiz>
}

const {Step} = Steps;


const steps: [StepContent1, StepContent2, StepContent3] = [
    {
        title: 'Mod. info. generale',
        content: {
            id: "",
            title: "",
            description: ""
        },
    },
    {
        title: 'Adauga lectile',
        newLesson: {
            id: "",
            lessonName: "",
            lessonContent: "",
            type: "new",
            deleted: false,
            modified: false,
        },
        lessons: {} as Record<string, Lesson>
    },

    {
        title: 'Adauga chestionare',
        quizzes: {
            "2131232": {
                deleted: false,
                modified: false,
                id: "2131232",
                questions: {
                    "abc-cde-fgh": {
                        id: "abc-cde-fgh",
                        title: "What question?",
                        answers: {
                            "asdasda": {
                                value: true,
                                id: "asdasda",
                                title: "Wow!",
                                modified: false,
                                deleted: false,
                            }
                        },
                        modified: false,
                        deleted: false,
                    }
                },
                quizContent: "",
                quizName: "sadsadsa",
                type: "existing",
            },
            "2131233": {
                deleted: false,
                modified: false,
                id: "2131233",
                questions: {
                    "abc-cde-fgg": {
                        id: "abc-cde-fgg",
                        title: "What question?",
                        answers: {
                            "asdasdf": {
                                value: true,
                                id: "asdasdf",
                                title: "Wow!",
                                modified: false,
                                deleted: false,
                            }
                        },
                        modified: false,
                        deleted: false,
                    }
                },
                quizContent: "",
                quizName: "sadsadsa",
                type: "existing",
            }
        } as Record<string, Quiz>
    },
];


export interface WizardStepsState  {
    steps: [StepContent1, StepContent2, StepContent3];
    currentStep: number;
}

export const renderModalContent = (state: any, setState: Function, next: Function, prev: Function) => {

    const {steps} = state;

    if (steps.length === 0) {
        return (<><p>LOADING...</p></>);
    }

    return (<>
        <Steps current={state.currentStep}>
            {steps.map((item: any) => (
                <Step key={item.title} title={item.title}/>
            ))}
        </Steps>
        <div className="steps-content">
            {renderFirstStep(state, setState)}
            {renderSecondStep(state, setState)}
            <ThirdStep state={state} setState={setState} />
        </div>
        <div className="steps-action">
            {state.currentStep < steps.length - 1 && (
                <Button type="primary" onClick={() => next()}>
                    Urmator
                </Button>
            )}
            {state.currentStep === steps.length - 1 && (
                <Button type="primary" onClick={() => {
                    console.log(state)
                    message.success('Processing complete!');
                }}>
                    Finalizeaza
                </Button>
            )}
            {state.currentStep > 0 && (
                <Button style={{margin: '0 8px'}} onClick={() => prev()}>
                    Anterior
                </Button>
            )}
        </div>
    </>)
}


export const WizardSteps = ({
                                id,
                                title,
                                content: description,
                                modalVisible,
                                toggleModal
                            }: { id: string, title: string, content: string, modalVisible: boolean, toggleModal: Function }) => {
    const [state, setState] = useState({steps: [] as Record<string, any>[], currentStep: 0}, 'wizard-steps');

    const next = () => {
        setState({...state, currentStep: state.currentStep + 1});
    };

    const prev = () => {
        setState({...state, currentStep: state.currentStep - 1});
    };

    const ok = () => {
        setState({currentStep: 0, steps: []})
        toggleModal(false);
    }
    const cancel = () => {
        setState({currentStep: 0, steps: []})
        toggleModal(false);
    }


    useEffect(() => {

        const [step1, step2] = steps;

        setTimeout(() => {
            step1.content = {
                id,
                title,
                description,
            }

           const backendData = [
                {
                    id: "123123-asdsads-sadasd-daadsa",
                    lessonName: "Test1",
                    lessonContent: "Content1"
                },
                {
                    id: "123123-asdsads-sadasd-daadsb",
                    lessonName: "Test2",
                    lessonContent: "Content2"
                },
                {
                    id: "123123-asdsads-sadasd-daadsc",
                    lessonName: "Test3",
                    lessonContent: "Content3"
                },
                {
                    id: "123123-asdsads-sadasd-daadsd",
                    lessonName: "Test4",
                    lessonContent: "Content4"
                },
                {
                    id: "123123-asdsads-sadasd-daadse",
                    lessonName: "Test5",
                    lessonContent: "Content5"
                }
            ];

            // enrichment phase: faza unde adaug niste proprietati suplimentare pe lectile ca
            // sa pot efectua operatiunile relative mai usor.

            step2.lessons = backendData.reduce((acc: Record<string, Lesson>, curr: Record<string, any>) => {
                acc[curr.id] = {
                    id: curr.id,
                    lessonName: curr.lessonName,
                    lessonContent: curr.lessonContent,
                    type: "existing",
                    deleted: false,
                    modified: false,
                }
                return acc;
            }, {} as Record<string, Lesson>)

            setState({...state, steps});
        }, 2000);

    }, [modalVisible]);

    return (
        <>
            <Modal
                title={title}
                centered={true}
                visible={modalVisible}
                onOk={ok}
                onCancel={cancel}
                width={"60vw"}
            >
                {renderModalContent(state, setState, next, prev)}
            </Modal>
        </>
    );
};
