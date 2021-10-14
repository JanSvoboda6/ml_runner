import { User } from "../types";

export default function authHeader()
{
    const user: User = JSON.parse(localStorage.getItem('user') || '{}');

    if (user && user.accessToken)
    {
        return { Authorization: 'Bearer ' + user.accessToken };
    } else
    {
        return {};
    }
}