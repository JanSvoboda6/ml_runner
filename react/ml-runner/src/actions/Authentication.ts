import AuthenticationService from "../services/AuthenticationService";
import { User } from "../types";
import { login as loginState, logout as logoutState } from '../redux/UserSlice';
import { useAppDispatch } from "../redux/hooks";

export const Register = (username: string, email: string, password: string): any => 
{
    return AuthenticationService.register(username, email, password);
};

export const Login = (user: User): User | any =>
{
    return AuthenticationService.login(user.username, user.password);
};

export const Logout = () =>
{
    const dispatch = useAppDispatch();
    dispatch(logoutState());
};