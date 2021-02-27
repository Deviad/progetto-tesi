import {Button, Collapse, Input, message, Modal, Steps, Typography} from 'antd';
import {useState} from "reinspect";
import React, {useEffect} from "react";
import ReactQuill from "react-quill";
import Title from "antd/es/typography/Title";
import Text from "antd/es/typography/Text";
import {noop} from 'lodash';

const {Step} = Steps;
const {Panel} = Collapse;
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
        lessons: []
    },
];


const renderFirstStep = (state: any, setState: Function) => {

    const onNameChange = (e: any) => {
        state.steps[0].content.title = e.target.value;

        setState({
            ...state,
            steps: [...state.steps]
        })


    }

    const handleEditorChange = (value: string) => {

        state.steps[0].content.description = value;

        setState({
            ...state,
            steps: [...state.steps]
        })
    }

    if (state.currentStep === 0) {
        return (
            <>
                <Typography>
                    <Title level={4}>
                        Denumire
                    </Title>
                </Typography>
                <Input name="name" onChange={onNameChange} value={state.steps[0].content.title}/>
                <Typography>
                    <Title level={4}>
                        Descriere
                    </Title>
                </Typography>
                <ReactQuill style={{background: "#fff"}} value={state.steps[0].content.description}
                            onChange={handleEditorChange}/>
                <br/>

            </>)
    }
}

const renderSecondStep = (state: any, setState: Function) => {
    if (state.currentStep === 1) {
        return (
            <>
                <br/>
                <div style={{overflowY: "scroll", height: "40vh"}}>
                    <Typography>
                        <Title level={5}>
                            Adauga o lectie
                        </Title>
                    </Typography>

                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                            Denumire
                        </Text>
                    </Typography>
                    <Input name="name" value="" style={{marginBottom: "0.5rem"}}/>
                    <Typography style={{marginBottom: "0.5rem"}}>
                        <Text style={{fontWeight: "bold"}}>
                           Continut
                        </Text>
                    </Typography>
                    <ReactQuill style={{background: "#fff"}} value=""
                                onChange={noop}/>
                    <br/>
                    <Typography>
                        <Title level={5}>
                            Lectile existente
                        </Title>
                    </Typography>
                    <Collapse accordion>
                        <Panel header="This is panel header 1" key="1">
                            <Typography style={{marginBottom: "0.5rem"}}>
                                <Text style={{fontWeight: "bold"}}>
                                    Denumire
                                </Text>
                            </Typography>
                            <Input name="name" value="" style={{marginBottom: "0.5rem"}}/>
                            <Typography style={{marginBottom: "0.5rem"}}>
                                <Text style={{fontWeight: "bold"}}>
                                   Continut
                                </Text>
                            </Typography>
                            <ReactQuill style={{background: "#fff"}} value=""
                                        onChange={noop}/>
                            <br/>
                        </Panel>
                        <Panel header="This is panel header 2" key="2">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                        <Panel header="This is panel header 3" key="3">
                            <p>Lorem ipsum</p>
                        </Panel>
                    </Collapse>




                </div>
                <br/>
            </>)
    }
};

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
                <Button type="primary" onClick={() => message.success('Processing complete!')}>
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

            setState({...state, steps: steps});
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
