import {Card, Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import React from "react";
import {EditOutlined} from "@ant-design/icons";
import "./Card.scss";
import "./CourseList.scss"
import {useState} from "reinspect";
import {WizardSteps} from "../coursemanagement/WizardSteps";

const renderCardList =
    ({courses, click}:
         {
             courses: { id: string; title: string, content: string }[],
             click: ({id, title, content}: { id: string, title: string, content: string }) => (e: any) => void
         }) => {

        const courseList = courses.map(c => {
            return (
                <Card
                    style={{display: "flex"}}
                    className="ant-card--ripeti"
                    key={c.id}
                    size="default"
                    actions={[
                        <EditOutlined key="edit" onClick={click({id: c.id, title: c.title, content: c.content})}/>
                    ]}
                >
                    <p>{c.title}</p>
                </Card>);
        });


        return (<>
            {courseList.length === 0 ?
                <p style={{display: 'flex', justifyContent: 'center'}}>Nu ai niciun curs adaugat</p> :
                courseList}
        </>)

    };


export const CourseList = () => {


    const [selected, select] = useState({id: "", title: "", content: ""}, "selected-course");
    const [toggled, toggleModal] = useState(false, "toggled-modal");


    const handleSelection = ({id, title, content}: { id: string, title: string, content: string }) => (e: any) => {
        select({id, title, content});
        toggleModal(true);
    }

    const courses = [
        {
            id: "asdas-asdsasa-asdsadsa",
            title: "Programare Mobila",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-bbbb",
            title: "Grafica pe calculator",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-bbbc",
            title: "Grafica pe calculator",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-bbbd",
            title: "Grafica pe calculator",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-ccccc",
            title: "Programare Mobila",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-eeeee",
            title: "Grafica pe calculator",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-fffff",
            title: "Grafica pe calculator",
            content: "Short description",
        },
        {
            id: "asdas-asdsasa-gggggg",
            title: "Grafica pe calculator",
            content: "Short description",
        }
    ];

    return (
        <Col flex="auto">
            <Row justify="center">
                <Col>
                    <Typography>
                        <Title level={3}>
                            Lista cursurilor
                        </Title>
                    </Typography>
                </Col>
            </Row>
            <br/>
            <br/>
            <Row justify="start">
                <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                     lg={{span: 22, flex: "auto", push: 2}} md={{span: 22, flex: "auto", push: 2}}
                     xs={{flex: "auto", span: 24}} style={{}}>
                    {renderCardList({courses, click: handleSelection})}
                </Col>
            </Row>
            {toggled &&
            <WizardSteps id={selected.id} title={selected.title} content={selected.content} modalVisible={toggled}
                         toggleModal={toggleModal}/>}
        </Col>)

}
