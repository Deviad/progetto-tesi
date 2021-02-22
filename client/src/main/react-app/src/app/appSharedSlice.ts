import {SharedState} from "../types";
import {createSlice, PayloadAction} from "@reduxjs/toolkit";


export const sharedInitialState = {
    error: null,
    isLoading: false,
}


function startLoading(state: SharedState) {
    state.isLoading = true
}

function stopLoading(state: SharedState) {
    state.isLoading = true
}

function loadingFailed(state: SharedState, action: PayloadAction<string>) {
    state.isLoading = false
    state.error = action.payload
}

function clearState(state: SharedState) {
    state.isLoading = false
    state.error = null
}


const shared = createSlice({
    name: 'shared',
    initialState: sharedInitialState,
    reducers: {
        getAppLoading: startLoading,
        getAppFailure: loadingFailed,
        resetAppState: clearState,
        getStopLoading: stopLoading,
    }
})

export const {
    getAppLoading,
    getStopLoading,
    getAppFailure,
    resetAppState,
} = shared.actions

export default shared.reducer

