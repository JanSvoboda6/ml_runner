import { configureStore } from '@reduxjs/toolkit'
import userReducer from './UserSlice'

import thunk from "redux-thunk";

/**
 * used for registering a reducers, that operate with state of the application when proper action is dispatched.
 */
export const store = configureStore({
    reducer: {
        user: userReducer
    },
    middleware: [thunk]
})

export type AppDispatch = typeof store.dispatch