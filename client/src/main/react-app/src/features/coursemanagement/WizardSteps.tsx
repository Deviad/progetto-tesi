import {Button, message, Modal, Steps} from 'antd';
import {useState} from "reinspect";
import React, {useEffect} from "react";
import {Lesson, Nullable, Quiz} from '../../types';
import {ThirdStep} from './steps/third';
import {FirstStep} from "./steps/first";
import {SecondStep} from "./steps/second/SecondStep";
import {utils} from "../../utils";
import {SecondStepSchema} from "./steps/second/secondStepCallbacks";
import {FirstStepSchema} from "./steps/first/firstStepCallbacks";
import {kebabCase} from "lodash";

export interface RipetiStep {

}

export interface NewLesson extends Lesson {
    type: "new";
    deleted: boolean;
    modified: boolean;
    errors: Record<string, any>;
}


export interface RipetiStep1Content {
    id: string;
    title: string;
    description: string;
    errors: Record<string, any>;
}


export interface RipetiStep1 extends RipetiStep {
    title: string;
    content: RipetiStep1Content;
}


export interface RipetiStep2 extends RipetiStep {
    title: string;
    newLesson: NewLesson
    lessons: Record<string, Lesson>;

}

export interface RipetiStep3 extends RipetiStep {
    title: string;
    quizzes: Record<string, Quiz>;
}

const {Step} = Steps;


const steps: [RipetiStep1, RipetiStep2, RipetiStep3] = [
    {
        title: 'Mod. info. generale',
        content: {
            id: "",
            title: "",
            description: "",
            errors: {}
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
            errors: {},
        },
        lessons: {} as Record<string, Lesson>,

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
                                errors: {},
                            }
                        },
                        modified: false,
                        deleted: false,
                        errors: {},
                    }
                },
                quizContent: "",
                quizName: "sadsadsa",
                type: "existing",
                errors: {}
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
                                errors: {},
                            }
                        },
                        modified: false,
                        deleted: false,
                        errors: {}
                    }
                },
                quizContent: "",
                quizName: "sadsadsa",
                type: "existing",
                errors: {}
            }
        } as Record<string, Quiz>,
    },
];


export interface WizardStepsState {
    steps: [RipetiStep1, RipetiStep2, RipetiStep3];
    currentStep: number;
    errors: Record<string, any>;
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
            <FirstStep state={state} setState={setState}/>
            <SecondStep state={state} setState={setState}/>
            <ThirdStep state={state} setState={setState}/>
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

            const usedNames: string[] = [];

            let errors: Nullable<Record<string, any>> = {};
            if (state.currentStep === 0) {
                errors = utils.validateFormBlock(state.steps[state.currentStep].content, FirstStepSchema);

            } else if (state.currentStep === 1) {
                for (const [key, lesson] of Object.entries((state.steps[state.currentStep].lessons as Lesson[]))) {
                    if(usedNames.some(l => l === kebabCase(lesson.lessonName.toLowerCase()))) {
                        message.error("Nu poti avea 2 lecti cu aceasi denumire");
                       return;
                    }
                    errors[kebabCase(lesson.lessonName.toLowerCase())] = utils.validateFormBlock(lesson, SecondStepSchema);
                    usedNames.push(kebabCase(lesson.lessonName.toLowerCase()));

                }
            }

            if (utils.isTrue(errors) && Object.keys(errors).length > 0) {

                let acc = [] as React.ReactNodeArray;

                for (const [eKey, eVal] of Object.entries(errors)) {
                    if (typeof eVal === "string") {
                        acc.push(<>{eVal} <br/></>);
                    } else if (Object.keys(eVal).length && Object.keys(eVal).length > 0) {
                         Object.values(eVal).forEach(val => acc.push(<>{eKey} -  {val} <br/></>));
                    }
                }

                if (acc.length > 0 ) {
                    message.error(Object.values(acc).map(x => x));
                    return;
                }
            }

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
                    errors: {},
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
    }
;
