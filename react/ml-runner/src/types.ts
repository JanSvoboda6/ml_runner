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