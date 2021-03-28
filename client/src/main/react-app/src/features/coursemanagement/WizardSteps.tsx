import {Button, Modal, Steps} from 'antd';
import {useState} from "reinspect";
import React, {useCallback, useEffect} from "react";
import {IFormError, ILesson, IQuiz, Nullable} from '../../types';
import {ThirdStep} from './steps/third';
import {FirstStep} from "./steps/first";
import {SecondStep} from "./steps/second/SecondStep";
import {next} from "./wizardStepsCallbacks";
import {httpGet} from '../../httpClient';
import {useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {BASE_URL, COURSE_ENDPOINT} from "../../constants";
import {useHistory} from "react-router-dom";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

export interface RipetiStep {

}

export interface NewLesson extends ILesson {
    id: string,
    lessonName: string;
    lessonContent: string;
    type: "new";
    modified: Nullable<boolean>,
    deleted: Nullable<boolean>,
    errors: Nullable<IFormError>;
}


export interface ICourse {
    id: string;
    title: string;
    description: string;
    modified: Nullable<boolean>;
    deleted: Nullable<boolean>;
    errors: Nullable<IFormError>;
    type: "new" | "existing"
}


export interface RipetiStep1 extends RipetiStep {
    title: string;
    content: ICourse;
}


export interface RipetiStep2 extends RipetiStep {
    title: string;
    newLesson: NewLesson
    lessons: Record<string, ILesson>;

}

export interface RipetiStep3 extends RipetiStep {
    title: string;
    quizzes: Record<string, IQuiz>;
}

const {Step} = Steps;


const steps: [RipetiStep1, RipetiStep2, RipetiStep3] = [
    {
        title: 'Mod. info. generale',
        content: {
            id: "",
            title: "",
            description: "",
            errors: {},
            type: "existing",
            deleted: false,
            modified: false,
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
        lessons: {} as Record<string, ILesson>,

    },
    {
        title: 'Adauga chestionare',
        quizzes: {} as Record<string, IQuiz>,
    },
];


export interface WizardStepsState {
    steps: [RipetiStep1, RipetiStep2, RipetiStep3];
    currentStep: number;
}

export const renderModalContent = (state: any, setState: Function, next: Function, prev: Function, accessToken: string) => {

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
                <Button type="primary" onClick={next(state, setState, state, accessToken)}>
                    Urmator
                </Button>
            )}
            {state.currentStep === steps.length - 1 && (
                <Button type="primary" onClick={next(state, setState, accessToken)}>
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
        const [state, mutableSetState] = useState({
            steps: ([] as Record<string, any>[]),
            currentStep: 0
        }, 'wizard-steps');

        const user = useSelector((state: RootState) => state.user)
        const history = useHistory();
        const setState = useCallback((state: any) => mutableSetState(state), []);

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

        const d = user.expiresAt && user.expiresAt * 1000;
        const expired = d && d <= Date.now();

        console.log("azz wizard", [d, Date.now(), d && d <= Date.now()]);

        useEffect(() => {

            const [step1, step2, step3] = steps;


            const init = async () => {
                if (expired) {
                    history.push("/login");
                }

                if (step1) {
                    step1.content = {
                        id,
                        title,
                        description,
                        errors: {},
                        type: "existing",
                        deleted: false,
                        modified: false,
                    }
                }
                let backendData;
                try {
                    backendData = await httpGet<Record<string, any>>({
                        headers: {
                            "Authorization": `Bearer ${user.accessToken}`,
                        },
                        url: `${BASE_URL}${COURSE_ENDPOINT}/${id}/getlessons`,
                    });
                } catch (error) {
                    console.log(error);
                }

                // const backendData = [
                //     {
                //         id: "123123-asdsads-sadasd-daadsa",
                //         lessonName: "Test1",
                //         lessonContent: "Content1"
                //     },
                //     {
                //         id: "123123-asdsads-sadasd-daadsb",
                //         lessonName: "Test2",
                //         lessonContent: "Content2"
                //     },
                //     {
                //         id: "123123-asdsads-sadasd-daadsc",
                //         lessonName: "Test3",
                //         lessonContent: "Content3"
                //     },
                //     {
                //         id: "123123-asdsads-sadasd-daadsd",
                //         lessonName: "Test4",
                //         lessonContent: "Content4"
                //     },
                //     {
                //         id: "123123-asdsads-sadasd-daadse",
                //         lessonName: "Test5",
                //         lessonContent: "Content5"
                //     }
                // ];

                // enrichment phase: faza unde adaug niste proprietati suplimentare pe lectile ca
                // sa pot efectua operatiunile relative mai usor.

                if (step2) {
                    if (backendData && backendData.body && Object.keys(backendData.body).length > 0) {
                        step2.lessons = Object.values(backendData?.body).reduce((acc: Record<string, ILesson>, curr: Record<string, any>) => {
                            acc[curr.id] = {
                                id: curr.id,
                                lessonName: curr.lessonName,
                                lessonContent: curr.lessonContent,
                                type: "existing",
                                deleted: false,
                                modified: false,
                                errors: {},
                            }
                            return acc;
                        }, {} as Record<string, ILesson>) || {};
                    }
                }

                setState({...state, steps: [step1, step2, step3]});
            }
            init();
        }, [modalVisible, expired]);

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
                    {renderModalContent(state, setState, next, prev, user.accessToken ? user.accessToken : "")}
                </Modal>
            </>
        );
    }
;
