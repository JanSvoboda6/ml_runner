import AuthenticationService from "./AuthenticationService";
import { User } from "../types";
import { login } from '../redux/UserSlice';

export function LoginService(dispatch: any, user: User)
{
    return (
        AuthenticationService.login(user.username, user.password).then(
            (user: any) =>
            {
                dispatch(login(user));
            },
        )
    );
}