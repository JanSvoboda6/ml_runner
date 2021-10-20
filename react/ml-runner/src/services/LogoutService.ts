
import { logout } from '../redux/UserSlice';
import AuthenticationService from './AuthenticationService';

function LogoutService(dispatch: any)
{
    AuthenticationService.logout();
    dispatch(logout());
}

export default LogoutService;