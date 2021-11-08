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

export interface FileInformation
{
    key: string,
    size?: number,
    modified?: number,
    data?: File
}