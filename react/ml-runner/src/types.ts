export interface User
{
    username: string,
    password: string,
    accessToken: string
}

export interface AuthorizationHeader
{
    Authorization: string
}

export interface AppState
{
    dispatch: any,
    history: any,
    isLoggedIn: boolean,
    message: any
}

export interface LoginState
{
    username: string,
    password: string,
    loading: boolean,
    isPopupClosed: boolean
}

export interface RegisterState
{
    username: string,
    email: string,
    password: string,
    isRegistrationSuccessful: boolean
}

export interface Action
{
    type: string,
    payload: any
}