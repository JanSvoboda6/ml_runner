import AuthenticationService from "./AuthenticationService";
import { User } from "../helpers/types";
import { login as doLogin} from '../redux/UserSlice';

/**
 * Used for calling a AuthenticationService and also dispatching the doLogin action so that state is changed (by reducer).
 */
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