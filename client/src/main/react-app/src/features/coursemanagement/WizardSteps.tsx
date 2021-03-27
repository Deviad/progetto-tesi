import {Button, Modal, Steps} from 'antd';
import {useState} from "reinspect";
import React, {useCallback, useEffect} from "react";
import {IFormError, ILesson, IQuiz, Nullable} from '../../types';
import {ThirdStep} from './steps/third';
import {FirstStep} from "./steps/first";
import {SecondStep} from "./steps/second/SecondStep";
import {next} from "./wizardStepsCallbacks";

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
  type: "new"|"existing"
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
                type: "existing",
              }
            },
            modified: false,
            deleted: false,
            errors: {},
            type: "existing",
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
                type: "existing",
              }
            },
            modified: false,
            deleted: false,
            errors: {},
            type: "existing",
          }
        },
        quizContent: "",
        quizName: "sadsadsa",
        type: "existing",
        errors: {},

      }
    } as Record<string, IQuiz>,
  },
];


export interface WizardStepsState {
  steps: [RipetiStep1, RipetiStep2, RipetiStep3];
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
      <FirstStep state={state} setState={setState}/>
      <SecondStep state={state} setState={setState}/>
      <ThirdStep state={state} setState={setState}/>
    </div>
    <div className="steps-action">
      {state.currentStep < steps.length - 1 && (
        <Button type="primary" onClick={next(state, setState)}>
          Urmator
        </Button>
      )}
      {state.currentStep === steps.length - 1 && (
        <Button type="primary" onClick={next(state, setState)}>
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


    useEffect(() => {

      const [step1, step2] = steps;

      setTimeout(() => {
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

        step2.lessons = backendData.reduce((acc: Record<string, ILesson>, curr: Record<string, any>) => {
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
        }, {} as Record<string, ILesson>)

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
