import
{
    REGISTER_SUCCESS,
    REGISTER_FAIL,
    LOGIN_SUCCESS,
    LOGIN_FAIL,
    LOGOUT,
    SET_MESSAGE,
} from "./ActionTypes";

import AuthenticationService from "../services/AuthenticationService";
import { User } from "../types";

export const register = (username: string, email: string, password: string) => (dispatch: (arg: { type: string; payload?: string; }) => void) =>
{
    return AuthenticationService.register(username, email, password).then(
        (response) =>
        {
            dispatch({
                type: REGISTER_SUCCESS,
            });

            dispatch({
                type: SET_MESSAGE,
                payload: response.data.message,
            });

            return Promise.resolve();
        },
        (error) =>
        {
            const message =
                (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                error.message ||
                error.toString();

            dispatch({
                type: REGISTER_FAIL,
            });

            dispatch({
                type: SET_MESSAGE,
                payload: message,
            });

            return Promise.reject();
        }
    );
};

export const login = (username: string, password: string) => (dispatch: (arg: { type: string; payload?: User | string; }) => void) =>
{
    return AuthenticationService.login(username, password).then(
        (user) =>
        {
            dispatch({
                type: LOGIN_SUCCESS,
                payload: user,
            });

            return Promise.resolve();
        },
        (error) =>
        {
            const message =
                (error.response && error.response.data && error.response.data.message)
                || error.message
                || error.toString();

            dispatch({
                type: LOGIN_FAIL,
            });

            dispatch({
                type: SET_MESSAGE,
                payload: message,
            });

            return Promise.reject();
        }
    );
};

export const logout = () => (dispatch: (arg: { type: string; }) => void) =>
{
    AuthenticationService.logout();

    dispatch({
        type: LOGOUT,
    });
};