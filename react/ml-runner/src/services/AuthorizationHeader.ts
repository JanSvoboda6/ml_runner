import { AuthorizationHeader as AuthorizationHeaderInterface, User } from "../types";

export default function AuthorizationHeader()
{
    const user: User = JSON.parse(localStorage.getItem('user') || '{}');

    if (user && user.accessToken)
    {
        var authorizationHeader: AuthorizationHeaderInterface = { Authorization: 'Bearer ' + user.accessToken };
        return authorizationHeader;
    } else
    {
        return {};
    }
}