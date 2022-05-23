import { logout as doLogout } from '../redux/UserSlice';
import AuthenticationService from './AuthenticationService';

/**
 * Used for calling a AuthenticationService and also dispatching the doLogout action so that state is changed (by reducer).
 */
const logout = (dispatch: any) =>
{
    AuthenticationService.logout();
    dispatch(doLogout());
}

export default {logout};