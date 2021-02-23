import React, {FC, useEffect, useState} from 'react';

import './App.css'
import {Col, Layout, Menu, PageHeader, Row} from "antd";
import Oauth2 from "./features/oauth2";
import {Registration} from "./features/registration/Registration";
import {Route, Switch, useHistory} from "react-router-dom";
import {ErrorComponent} from "./features/common/Error";
import {AuthGuard} from "./features/oauth2/AuthGuard";
import * as H from 'history';
import {utils} from "./utils";
import {useSelector} from "react-redux";
import {RootState} from "./app/rootReducer";

const NotFound = () => <div>Page not Found</div>;


const App: FC = (props: any) => {

    const history: H.History = useHistory();

    const [menuState, setMenuState] = useState<Record<string, string>>({});

    const currentPage = useSelector((state: RootState) => state.shared.currentPage);

    const handleClick = (e: any) => {
        console.log('click ', e);
        setMenuState({current: e.key});
    };

    const guardedPaths = ["/user-profile"]

    useEffect(() => {
        setMenuState({current: currentPage === null ? "" : currentPage as unknown as string});
    }, [currentPage])

    const renderLogOutMenuButton = () => {
        const user = utils.storage.getItem("auth_res");
        return utils.isTrue(user) &&
            <Menu.Item key="logout" onClick={() => history.push("/logout")}>
                Logout
            </Menu.Item>;
    }

    return (
        <Layout>
            <Layout.Header className="header">
                <Menu onClick={handleClick} selectedKeys={[menuState.current]} mode="horizontal" theme="dark" style={{
                    display: "flex",
                    justifyContent: "flex-end"
                }}>
                    <Menu.Item key="home" onClick={() => history.push("/")}>
                        Home
                    </Menu.Item>
                    <Menu.Item key="register" onClick={() => history.push("/register")}>
                        Register
                    </Menu.Item>
                    {renderLogOutMenuButton()}
                    <Menu.Item key="user-profile" onClick={() => history.push("/user-profile")}>
                        User Profile
                    </Menu.Item>
                </Menu>
            </Layout.Header>
            <PageHeader>
            </PageHeader>
            <Layout.Content>
                <Row gutter={[16, 16]} align="middle" justify="center">
                    <Col span={16} flex="auto">
                        <Switch>
                            <Route exact path="/">
                                <div>Home Page</div>
                            </Route>

                            <Route path="/oauth">
                                <Oauth2/>
                            </Route>

                            {guardedPaths.map(r => (
                                <Route path={r}>
                                    <AuthGuard/>
                                </Route>
                            ))}

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
