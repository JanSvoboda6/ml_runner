import AuthenticationService from "../services/AuthenticationService";

const register = (username: string, password: string): any =>
{
    return AuthenticationService.register(username, password);
};

export default {register};