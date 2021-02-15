import { combineReducers } from '@reduxjs/toolkit'
import usersCommonSlice from "../features/userCommon/userCommonSlice";
import appSharedSlice from "./appSharedSlice";


const rootReducer = combineReducers({
    user: usersCommonSlice,
    shared: appSharedSlice,
})

export type RootState = ReturnType<typeof rootReducer>

export default rootReducer
