import React, {FC, useEffect} from "react";
import {Route, Switch, useHistory} from "react-router-dom";
import UserProfile from "../userProfile";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../app/rootReducer";
import {utils} from "../../utils";
import {getUserSuccess} from "../userCommon/userCommonSlice";
import {PagePathName} from "../../types";
import {Dashboard} from "../dashboard";


export const AuthGuard: FC = () => {

    const user = useSelector((state: RootState) => state.user);
    const dispatch = useDispatch();
    const history = useHistory();
    useEffect(() => {
        if (user.email == null) {
            if(utils.isValidStoredToken()) {
                const userState = utils.getUserStateFromAuthResponse(utils.decodeAuthResp(utils.storage.getItem("auth_res") as string));
                dispatch(getUserSuccess(userState));
            }
            else {
                history.push('/oauth/authorization');
            }
        }

    }, [user.email])

    return (
        <Switch>
            <Route path={PagePathName.USER_PROFILE}>
                <UserProfile/>
            </Route>
            <Route path="/dashboard">
                <Dashboard />
            </Route>
        </Switch>
    );
}
