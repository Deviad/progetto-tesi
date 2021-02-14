import { combineReducers } from '@reduxjs/toolkit'
import usersCommonSlice from "../features/userCommon/userCommonSlice";


const rootReducer = combineReducers({
    user: usersCommonSlice,
})

export type RootState = ReturnType<typeof rootReducer>

export default rootReducer
