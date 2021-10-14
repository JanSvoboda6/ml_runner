import { AuthorizationHeader, User } from "../types";

export default function authHeader()
{
    const user: User = JSON.parse(localStorage.getItem('user') || '{}');

    if (user && user.accessToken)
    {
        var authothorizationHeader: AuthorizationHeader = { Authorization: 'Bearer ' + user.accessToken };
        return authothorizationHeader;
    } else
    {
        return {};
    }
}