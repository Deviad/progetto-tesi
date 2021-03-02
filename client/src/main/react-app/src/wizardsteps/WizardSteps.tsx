import {Button, Collapse, message, Modal, Steps} from 'antd';
import {useState} from "reinspect";
import React, {useEffect} from "react";
import {Lesson} from '../types';
import {renderFirstStep} from "./renderFirstStep";
import {renderSecondStep} from "./renderSecondStep";

const {Step} = Steps;
const steps: Record<string, any>[] = [
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
        content: "Second-content",
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
];

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
        </div>
        <div className="steps-action">
            {state.currentStep < steps.length - 1 && (
                <Button type="primary" onClick={() => next()}>
                    Urmator
                </Button>
            )}
            {state.currentStep === steps.length - 1 && (
                <Button type="primary" onClick={() => {

                    console.log(state.steps[1].lessons)

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
        setTimeout(() => {
            steps[0].content = {
                id,
                title,
                description,
            }

            steps[1].lessons = [
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

            steps[1].lessons = steps[1].lessons.reduce((acc: Record<string, Record<string, any>>, curr: Record<string, any>) => {
                acc[curr.id] = {
                    id: curr.id,
                    lessonName: curr.lessonName,
                    lessonContent: curr.lessonContent,
                    type: "existing",
                    deleted: false,
                    modified: false,
                }
                return acc;
            }, {} as Record<string, Record<string, any>>)

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
