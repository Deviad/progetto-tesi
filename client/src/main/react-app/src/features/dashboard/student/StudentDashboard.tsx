import React, {FC, useEffect} from 'react';
import {Col, Menu, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import {CourseListStudent} from './CourseListStudent';
import {Route, Switch, useHistory} from "react-router-dom";
import {ListType} from "./ListType";


export const StudentDashboard: FC = () => {

    const history = useHistory();

    useEffect(() => {
        history.push('/dashboard/student/listallcourses');
    });

    return (<>
        <Row gutter={[16, 16]} align="middle" justify="center">
            <Col span={24} style={{display: "flex", justifyContent: "center"}}>
                <Typography>
                    <Title level={3}>
                        Panoul de operare (student)
                    </Title>
                </Typography>
            </Col>
        </Row>
        <br/>
        <br/>
        <Row gutter={[16, 16]} align="middle" justify="center" style={{display: "flex", alignItems: "flex-start"}}>
            <Menu
                theme="dark"
                defaultSelectedKeys={['add']}
                mode="horizontal"
            >
                <Menu.Item key="add" onClick={() => history.push('/dashboard/student/listallcourses')}>
                    Toate cursurile
                </Menu.Item>
                <Menu.Item key="list" onClick={() => history.push('/dashboard/student/listenrolledcourses')}>
                   Inregistrat
                </Menu.Item>
            </Menu>
        </Row>
        <br/>
        <br/>
        <Row gutter={[16, 16]} align="middle" justify="center" style={{display: "flex", alignItems: "flex-start"}}>
            <Switch>
                <Route path="*/student/listallcourses">
                    <CourseListStudent type={ListType.ALL_COURSES}/>
                </Route>
                <Route path="*/student/listenrolledcourses">
                    <CourseListStudent type={ListType.ENROLLED_COURSES}/>
                </Route>
            </Switch>
        </Row>
    </>)
}
