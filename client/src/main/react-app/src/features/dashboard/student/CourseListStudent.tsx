import {Anchor, Card, Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import React, {useEffect} from "react";
import {EyeOutlined} from "@ant-design/icons";
import "../Card.scss";
import "./CourseListStudent.scss"
import {useState} from "reinspect";
import {useSelector} from "react-redux";
import {RootState} from "../../../app/rootReducer";
import {httpGet} from "../../../httpClient";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {useHistory} from "react-router-dom";
import {CoursePreview} from "./CoursePreview";
import {getUrl, ListType} from "./ListType";
const { Link } = Anchor;

dayjs.extend(utc);

const renderCardList =
    ({courses, click, titleClick}:
         {
             courses: { id: string; title: string, content: string, teacherName: string }[],
             click: ({
                         id,
                         title,
                         content,
                         teacherName
                     }: { id: string, title: string, content: string, teacherName: string }) => (e: any) => void

             titleClick: ({id}: {id: string}) => (e: React.MouseEvent) => void

         }) => {

        const courseList = courses.map(c => {
            return (
                <Card
                    style={{display: "flex"}}
                    className="ant-card--ripeti"
                    key={c.id}
                    size="default"
                    actions={[
                        <EyeOutlined
                            key="preview"
                            onClick={click({
                                id: c.id,
                                title: c.title,
                                content: c.content,
                                teacherName: c.teacherName
                            })}/>
                    ]}
                >
                <Anchor affix={false} onClick={titleClick({id: c.id})}>
                     <Link href="#" title={c.title}  />
                </Anchor>
                </Card>);
        });


        return (<>
            {courseList.length === 0 ?
                <p style={{display: 'flex', justifyContent: 'center'}}>Nu ai niciun curs adaugat</p> :
                courseList}
        </>)

    };


export const CourseListStudent = ({type}: { type: ListType }) => {

    const [selected, select] = useState({id: "", title: "", content: "", teacherName: ""}, "selected-course");
    const [toggled, toggleModal] = useState(false, "toggled-modal");
    const history = useHistory();

    const handleSelection = ({id, title, content, teacherName}:
                                 { id: string, title: string, content: string, teacherName: string }) =>
        (e: any) => {
            select({id, title, content, teacherName});

            toggleModal(true);
        }

    const handleTitleClick = ({id}: {id: string}) => (e: React.MouseEvent) => {
        e.preventDefault();
        history.push(`coursedetail/${id}`);
    }

    const user = useSelector((state: RootState) => state.user)
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
                    url: getUrl(type),
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


    }, [user.accessToken, expired, type]);


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
                    {renderCardList({courses, click: handleSelection, titleClick: handleTitleClick})}
                </Col>
            </Row>
            {toggled &&
            <CoursePreview
                id={selected.id}
                title={selected.title}
                content={selected.content}
                teacherName={selected.teacherName}
                modalVisible={toggled}
                toggleModal={toggleModal}/>}
        </Col>)

}
