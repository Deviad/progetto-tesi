import React, {FC, useEffect} from 'react';
import {Col, Menu, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import 'react-quill/dist/quill.snow.css';
import "./ProfessorDashboard.scss";
import {Route, Switch, useHistory} from "react-router-dom";
import {CourseAddition} from "../CourseAddition";
import {CourseListProfessor} from "./CourseListProfessor";

export const ProfessorDashboard: FC = () => {

    const history = useHistory();

    useEffect(() => {
        history.push('/dashboard/professor/addcourse');
    });

    return (
        <>
            <Row gutter={[16, 16]} align="middle" justify="center">
                <Col span={24} style={{display: "flex", justifyContent: "center"}}>
                    <Typography>
                        <Title level={3}>
                            Panoul de operare (profesor)
                        </Title>
                    </Typography>
                </Col>
            </Row>
            <br/>
            <br/>
            <Row gutter={[16, 16]} align="middle" justify="center" style={{display: "flex", alignItems: "flex-start"}}>
                <Menu
                    theme="dark"
                    // style={{width: 200}}
                    defaultSelectedKeys={['add']}
                    mode="horizontal"
                >
                    <Menu.Item key="add" onClick={() => history.push('/dashboard/professor/addcourse')}>Adauga
                        curs</Menu.Item>
                    <Menu.Item key="list" onClick={() => history.push('/dashboard/professor/listcourses')}>Lista
                        cursurilor</Menu.Item>
                </Menu>
            </Row>
            <br/>
            <br/>
            <Row gutter={[16, 16]} align="middle" justify="center" style={{display: "flex", alignItems: "flex-start"}}>
                <Switch>
                    <Route path="*/professor/addcourse">
                        <CourseAddition/>
                    </Route>
                    <Route path="*/professor/listcourses">
                        <CourseListProfessor/>
                    </Route>
                </Switch>
            </Row>
        </>
    )
}
