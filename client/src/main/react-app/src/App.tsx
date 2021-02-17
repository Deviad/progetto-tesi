import React, {FC} from 'react';

import './App.css'
import {Col, Layout, PageHeader, Row} from "antd";
import Oauth2 from "./features/oauth2";
import {Registration} from "./features/registration/Registration";
import {Login} from "./features/login/Login";
import {Route, Switch} from "react-router-dom";
import {ErrorComponent} from "./features/common/Error";
import {AuthGuard} from "./features/oauth2/AuthGuard";


const NotFound = () => <div>Page not Found</div>;

const App: FC = () => {

    const guardedPaths = ["/user-profile"]

    return (
        <Layout>
            <Layout.Header>
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
                            <Route path="/login">
                                <Login/>
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
