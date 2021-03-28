import React, {useCallback, useEffect, useRef} from 'react';

// @ts-ignore
import {useRoutes} from 'hookrouter';
import {utils} from "../../utils";
import {shallowEqual, useDispatch, useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {fetchUser} from "../userCommon/userCommonSlice";
import {Nullable, IUser} from "../../types";
import {Route, useHistory} from "react-router-dom";


const Authorization = () => {
    console.log('NODE_ENV', process.env.NODE_ENV);
    const urlencodedParams = utils.toFormUrlEncoded({
        "redirect_uri": utils.endpointFactory().tokenRedirect
        // "state": oauth2Ely5Utils.cookieStorage.getItem("XSRF-TOKEN")
    });

    const url = `http://localhost:8080/custom-oauth/auth/?${urlencodedParams}`;

    useEffect(() => {
        document.location.href = url;
    }, []);
    return (<div>Authorization</div>);
};

const Token = () => {

    const cache = useRef<Nullable<IUser>>(null);

    const dispatch = useDispatch();
    const history = useHistory();

    const user = useSelector((state: RootState) => state.user);
    const cFetchUser = useCallback((user, history) => dispatch(fetchUser(user, history)), []);

    const d = user.expiresAt && user.expiresAt * 1000;
    const expired = d && d <= Date.now();

    useEffect(() => {
        console.log("storedUser", utils.storage.getItem("auth_res"));
        if (shallowEqual(user, cache.current) && !expired) {
            return;
        }

        cFetchUser(user, history);
        cache.current = user;
    }, [user, expired]);
    return (<div>Token</div>);
};

const MemoizedToken = React.memo(Token);

const OAuth2 = () =>

    (<>
        <Route path="*/authorization">
            <Authorization/>
        </Route>
        <Route path="*/token">
            <MemoizedToken/>
        </Route>
    </>);

export default React.memo(OAuth2);
