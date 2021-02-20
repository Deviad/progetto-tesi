import {createSlice, Dispatch, PayloadAction} from '@reduxjs/toolkit'

import {AppThunk} from "../../app/store";
import {AuthorizationResponse, UserState} from "../../types";
import {utils} from "../../utils";
import * as H from 'history';
import {getAppFailure, getAppLoading} from '../../app/appSharedSlice';

export const userInitialState: UserState = {
    username: null,
    email: null,
    expirationTime: null,
    accessToken: null,
    refreshToken: null,
    refreshExpirationTime: null,
    issuedAt: null,
    expiresAt: null,
    firstName: null,
    lastName: null,
}


const user = createSlice({
    name: 'user',
    initialState: userInitialState,
    reducers: {
        getUserSuccess(state, {payload}: PayloadAction<UserState>) {
            const {username, email, issuedAt, expiresAt, expirationTime, accessToken, refreshToken, refreshExpirationTime, firstName, lastName} = payload
            state.username = username;
            state.email = email;
            state.expirationTime = expirationTime;
            state.issuedAt = issuedAt;
            state.expiresAt = expiresAt;
            state.accessToken = accessToken;
            state.refreshToken = refreshToken;
            state.refreshExpirationTime = refreshExpirationTime;
            state.firstName = payload.firstName;
            state.lastName = payload.lastName;
        },
    }
})

export const {
    getUserSuccess,
} = user.actions

export default user.reducer

export const fetchUser = (
    user: UserState,
    history: H.History
): AppThunk => async (dispatch: Dispatch) => {

    try {
        dispatch(getAppLoading());
        const currentUser = await handleLocalAuth(user);
        dispatch(getUserSuccess(currentUser));
        history.push("/user-profile")
    } catch (err) {
        dispatch(getAppFailure(err.toString()))
        history.push("/error")
    }
}


const handleLocalAuth = (user: UserState): Promise<UserState> => {

    if (user.email != null) {
        return Promise.resolve(user);
    }

    const urlencodedParams = utils.toFormUrlEncoded({
        'grant_type': 'authorization_code',
        'code': (utils.getParameterByName("code") as string),
        'redirect_uri': utils.endpointFactory().tokenRedirect,
        'client_id': 'ripeti-web',
        'client_secret': 'b8cc4bab-c1c5-4af4-9456-bec7f81a5bda',
    });

    const authorizeUrl = `http://localhost:8884/auth/realms/ripeti/protocol/openid-connect/token`;
    return fetch(authorizeUrl, {
        // credentials: "include",
        method: "POST",
        mode: 'cors',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            // 'Target-URL': `http://localhost:8884/auth/realms/agrilink/protocol/openid-connect/token`
            // "Authorization": `Basic '${+btoa("fooClientIdPassword:secret")}'`
        },
        redirect: 'follow',
        body: urlencodedParams
    })
        .then(res => {
            if (((res.status / 100) | 0) === 2) {
                return res;
            }
            if (res.status === 0) {
                throw new Error("Cannot establish a connection or CORS error")
            }
            throw new Error(JSON.stringify(res));
        })
        .catch(error => {
            console.log('Request failed', error);
            throw new Error(error.message);
        })
        .then(res => (res as Response).json())
        .then((auth: AuthorizationResponse) => {
            const result = utils.getUserStateFromAuthResponse(auth);
            console.log('Request succeeded with JSON response', auth);
            utils.storage.setItem("auth_res", JSON.stringify(auth));
            return result as UserState;
        })
};
