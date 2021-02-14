import React, {useEffect} from "react";
// @ts-ignore
import { useHistory } from "react-router-dom";


export const Login = () => {
    const history = useHistory();

    useEffect(() => {
        setTimeout(() => {
            history.push('/oauth/authorization')
        }, 1000);
    }, []);

    return <div>This is a showcase of OAuth2 integration in React</div>

};
