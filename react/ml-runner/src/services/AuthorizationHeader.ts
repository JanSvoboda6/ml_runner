import { AuthorizationHeader as AuthorizationHeaderInterface, User } from "../helpers/types";

/**
 * Holds JWT token that is needed for authorization when communicating with API.
 */
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