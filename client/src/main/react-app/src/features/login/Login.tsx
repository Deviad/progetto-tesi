import React, {useEffect} from "react";
// @ts-ignore
import {useHistory} from "react-router-dom";


export const Login = () => {
    const history = useHistory();

    useEffect(() => {
        history.push('/oauth/authorization');
    }, []);

    return <div>This is a showcase of OAuth2 integration in React</div>

};
