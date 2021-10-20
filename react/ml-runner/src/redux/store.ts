import { configureStore } from '@reduxjs/toolkit'
import userReducer from './UserSlice'

import thunk from "redux-thunk";

export const store = configureStore({
    reducer: {
        user: userReducer
    },
    middleware: [thunk]
})

//TODO Jan: How to use these states?
export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch