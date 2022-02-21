import AuthenticationService from "./AuthenticationService";
import { User } from "../types";
import { login as doLogin} from '../redux/UserSlice';

const login = (dispatch: any, user: User) =>
{
    return (
        AuthenticationService.login(user.username, user.password).then(
            (user: any) =>
            {
                dispatch(doLogin(user));
            },
        )
    );
}

export default {login};