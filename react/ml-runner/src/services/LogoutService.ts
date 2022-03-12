
import { logout as doLogout } from '../redux/UserSlice';
import AuthenticationService from './AuthenticationService';

const logout = (dispatch: any) =>
{
    AuthenticationService.logout();
    dispatch(doLogout());
}

export default {logout};