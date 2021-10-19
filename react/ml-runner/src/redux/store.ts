import { configureStore } from '@reduxjs/toolkit'
import userReducer from './UserSlice'

import thunk from "redux-thunk";

export const store = configureStore({
    reducer: {
        user: userReducer
    },
    middleware: [thunk]
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch