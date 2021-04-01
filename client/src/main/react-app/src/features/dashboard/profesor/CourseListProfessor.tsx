import {Card, Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import React, {useEffect} from "react";
import {EditOutlined} from "@ant-design/icons";
import "../Card.scss";
import "./CourseListProfessor.scss"
import {useState} from "reinspect";
import {WizardSteps} from "../../coursemanagement/WizardSteps";
import {useSelector} from "react-redux";
import {RootState} from "../../../app/rootReducer";
import {httpGet} from "../../../httpClient";
import {BASE_URL, COURSE_ENDPOINT} from "../../../constants";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {useHistory} from "react-router-dom";

dayjs.extend(utc);

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


export const CourseListProfessor = () => {


    const [selected, select] = useState({id: "", title: "", content: ""}, "selected-course");
    const [toggled, toggleModal] = useState(false, "toggled-modal");


    const handleSelection = ({id, title, content}: { id: string, title: string, content: string }) => (e: any) => {
        select({id, title, content});
        toggleModal(true);
    }


    const user = useSelector((state: RootState) => state.user)
    const history = useHistory();
    const d = user.expiresAt && user.expiresAt * 1000;
    const expired = d && d <= Date.now();
    const [courses, setCourses] = useState([], 'loading-courses');

    useEffect(() => {

        const init = async () => {
            if (expired) {
                history.push("/login")
            }

            let backendData: NonNullable<any>;
            try {
                backendData = await httpGet<Record<string, any>[]>({
                    headers: {
                        "Authorization": `Bearer ${user.accessToken}`,
                    },
                    url: `${BASE_URL}${COURSE_ENDPOINT}/getbyteacher`,
                });

                if (backendData && backendData.body && backendData.body.length > 0) {
                    setCourses(backendData.body.map((row: Record<string, string>) => {
                        return {
                            id: row.courseId,
                            title: row.courseName,
                            content: row.courseDescription,
                            status: row.courseStatus,
                            teacherId: row.teacherId,
                            errors: {},
                            deleted: false,
                            type: "existing",
                            teacherName: row.teacherName,
                        }
                    }));
                }

            } catch (error) {
                console.log(error);
            }
        }

        init();


    }, [user.accessToken, expired]);


    if (!courses || courses.length === 0) {
        return <div>Loading Courses ...</div>
    }

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
