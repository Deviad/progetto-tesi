import React, {FC, useEffect, useState} from 'react';

import './App.css'
import {Col, Layout, Menu, PageHeader, Row} from "antd";
import Oauth2 from "./features/oauth2";
import {Registration} from "./features/registration/Registration";
import {Route, Switch, useHistory} from "react-router-dom";
import {ErrorComponent} from "./features/common/Error";
import {AuthGuard} from "./features/oauth2/AuthGuard";
import * as H from 'history';
import {useSelector} from "react-redux";
import {RootState} from "./app/rootReducer";
import {PagePathName, PageSlug} from "./types";
import {Login} from "./features/login/Login";
import {
    handleClick,
    renderLogInMenuButton,
    renderDashboardMenuButton,
    renderRegisterMenuButton,
    renderUserProfileButton, renderLogOutMenuButton
} from "./appServices";
import {Logout} from "./features/logout/Logout";
import {Dashboard} from "./features/dashboard/Dashboard";
import {Home} from "./features/home/Home";

const NotFound = () => <div>Page not Found</div>;

const App: FC = () => {

    const history: H.History = useHistory();

    const [menuState, setMenuState] = useState<Record<string, string>>({});

    const currentPage = useSelector((state: RootState) => state.shared.currentPage);

    const guardedPaths = [PagePathName.USER_PROFILE]

    useEffect(() => {
        setMenuState({current: currentPage === null ? "" : currentPage as unknown as string});
    }, [currentPage])


    return (
        <Layout>
            <Layout.Header className="header">
                <Menu onClick={handleClick(setMenuState)} selectedKeys={[menuState.current]} mode="horizontal"
                      theme="dark" style={{
                    display: "flex",
                    justifyContent: "flex-end"
                }}>
                    <Menu.Item key={PageSlug.HOME} onClick={() => history.push("/")}>
                        Home
                    </Menu.Item>
                    {renderRegisterMenuButton(history)}
                    {renderLogInMenuButton(history)}
                    {renderLogOutMenuButton(history)}
                    {renderDashboardMenuButton(history)}
                    {renderUserProfileButton(history)}
                </Menu>
            </Layout.Header>
            <PageHeader>
            </PageHeader>
            <Layout.Content>
                <Row gutter={[16, 16]} align="middle" justify="center">
                    <Col span={16} flex="auto">
                        <Switch>

                            {["/", PagePathName.HOME].map(path => (
                                <Route exact path={path}>
                                    <Home />
                                </Route>
                            ))}

                            <Route path="/oauth">
                                <Oauth2/>
                            </Route>

                            {guardedPaths.map(r => (
                                <Route path={r}>
                                    <AuthGuard/>
                                </Route>
                            ))}

                            <Route path="/dashboard">
                                <Dashboard />
                            </Route>

                            <Route path="/login">
                                <Login/>
                            </Route>

                            <Route path="/logout">
                                <Logout/>
                            </Route>

                            <Route path="/register">
                                <Registration/>
                            </Route>

                            <Route path="/error">
                                <ErrorComponent/>
                            </Route>
                            <Route path={`*`} component={NotFound}/>
                        </Switch>
                    </Col>
                </Row>
            </Layout.Content>

        </Layout>
    );
}

export default App;
