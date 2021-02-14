import React from "react";
import { Route } from "react-router-dom";
import * as H from 'history';


const Refresh = ({ path = "/", history, location }: {path: string, history: H.History, location: Location}) => (
    <Route
        path={path}
        component={() => {
            history.replace({
                ...location,
                pathname: location.pathname.split("/refresh")[1]
            });
            return null;
        }}
    />
);

export default Refresh;
