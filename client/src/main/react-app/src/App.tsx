import React, {FC} from 'react';

import './App.css'
import {Layout, PageHeader} from "antd";
import UserProfile from "./features/userProfile";
import Oauth2 from "./features/oauth2";
import {Registration} from "./features/registration/Registration";
import {Login} from "./features/login/Login";
import {Route, Switch} from "react-router-dom";


const NotFound = () => <div>Page not Found</div>;

const App: FC = () => {

    return (
        <Layout>
            <Layout.Header>
            </Layout.Header>
            <PageHeader>
            </PageHeader>
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
                <Route path="/user-profile">
                    <UserProfile />
                </Route>
                <Route path="/register">
                    <Registration/>
                </Route>
                <Route path={`*`} component={NotFound} />
            </Switch>
        </Layout>
    );
}

export default App;
