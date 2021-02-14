import React, {useCallback, useEffect, useMemo, useRef} from 'react';

// @ts-ignore
import {useRoutes} from 'hookrouter';
import {utils} from "../../utils";
import {shallowEqual, useDispatch, useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {fetchUser} from "../userCommon/userCommonSlice";
import {Nullable, User} from "../../types";
import {Route, useHistory} from "react-router-dom";


const Authorization = () => {
    console.log('NODE_ENV', process.env.NODE_ENV);
    const urlencodedParams = utils.toFormUrlEncoded({
        "response_type": "code",
        "client_id": "ripeti-web",
        'scope': 'openid',
        "redirect_uri": utils.endpointFactory().tokenRedirect
        // "state": oauth2Ely5Utils.cookieStorage.getItem("XSRF-TOKEN")
    });

    const url = `http://localhost:8884/auth/realms/ripeti/protocol/openid-connect/auth/?${urlencodedParams}`;

    useEffect(() => {
        document.location.href = url;
    }, []);
    return (<div>Authorization</div>);
};


const Token = React.memo(() => {

    const cache = useRef<Nullable<User>>(null);

    const dispatch = useDispatch();
    const history = useHistory();

    const user = useSelector((state: RootState) => state.user);
    const cFetchUser = useCallback((user, history) => dispatch(fetchUser(user, history)), [dispatch, history]);

    if(shallowEqual(user, cache.current)) {
        return <div>Token</div>;
    }

    useEffect(() => {
        cFetchUser(user, history)
    }, [user]);

    useEffect(() => {
        cache.current = user;
    });

    return (<div>Token</div>);

});

const OAuth2 = () =>

     (<>
        <Route path="*/authorization">
            <Authorization/>
        </Route>
        <Route path="*/token">
            <Token/>
        </Route>
    </>);

export default React.memo(OAuth2);
