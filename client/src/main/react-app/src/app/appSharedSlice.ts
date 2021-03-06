import {ISharedState} from "../types";
import {createSlice, PayloadAction} from "@reduxjs/toolkit";


export const sharedInitialState = {
    error: null,
    isLoading: false,
    currentPage: null,
}


function startLoading(state: ISharedState) {
    state.isLoading = true
}

function stopLoading(state: ISharedState) {
    state.isLoading = true
}

function loadingFailed(state: ISharedState, action: PayloadAction<string>) {
    state.isLoading = false
    state.error = action.payload
}

function clearState(state: ISharedState) {
    state.isLoading = false
    state.error = null
}

function setCurrentPage(state: ISharedState, action: PayloadAction<string>) {
    state.currentPage = action.payload;
}


const shared = createSlice({
    name: 'shared',
    initialState: sharedInitialState,
    reducers: {
        getAppLoading: startLoading,
        getAppFailure: loadingFailed,
        resetAppState: clearState,
        getStopLoading: stopLoading,
        getSetCurrentPage: setCurrentPage
    }
})

export const {
    getAppLoading,
    getStopLoading,
    getAppFailure,
    resetAppState,
    getSetCurrentPage,
} = shared.actions

export default shared.reducer

