import {createSlice, Dispatch, PayloadAction} from '@reduxjs/toolkit'

import {AppThunk} from "../../app/store";
import {User, userInitialState, UserState} from "../../types";
import {utils} from "../../utils";
import * as H from 'history';

function startLoading(state: UserState) {
    state.isLoading = true
}

function loadingFailed(state: UserState, action: PayloadAction<string>) {
    state.isLoading = false
    state.error = action.payload
}

const user = createSlice({
    name: 'user',
    initialState: userInitialState,
    reducers: {
        getUserStart: startLoading,
        getUserSuccess(state, {payload}: PayloadAction<User>) {
            const {username, email} = payload
            state.username = username;
            state.email = email;
            state.isLoading = false
            state.error = null
        },
        getUserFailure: loadingFailed,
    }
})

export const {
    getUserStart,
    getUserSuccess,
    getUserFailure,
} = user.actions

export default user.reducer

export const fetchUser = (
    user: User,
    history: H.History
): AppThunk => async (dispatch: Dispatch) => {

    try {
        dispatch(getUserStart());
        const currentUser = await handleLocalAuth(user);
        dispatch(getUserSuccess(currentUser));
        history.push("/user-profile")
    } catch (err) {
        dispatch(getUserFailure(err.toString()))
        throw new Error("Could not fetch token: " + err);
    }
}


const handleLocalAuth = (user: User): Promise<User> => {

    if (user?.email != null) {
        return Promise.resolve(user);
    }

    const urlencodedParams = utils.toFormUrlEncoded({
        'grant_type': 'authorization_code',
        'code': (utils.getParameterByName("code") as string),
        'redirect_uri': utils.endpointFactory().tokenRedirect,
        'client_id': 'ripeti-web',
        'client_secret': 'b8cc4bab-c1c5-4af4-9456-bec7f81a5bda',
    });

    const authorizeUrl = `http://localhost:8884/auth/realms/agrilink/protocol/openid-connect/token`;
    return fetch(authorizeUrl, {
        // credentials: "include",
        method: "POST",
        mode: 'no-cors',
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

            throw new Error(JSON.stringify(res));


        })
        .catch(error => {
            console.log('Request failed', error);
            throw new Error(error);
        })
        .then(res => (typeof res === 'object') && res.json())
        .then((user: User) => {
            console.log('Request succeeded with JSON response', user);
            utils.storage.setItem("user", JSON.stringify(user));
            return user;
        })

};
