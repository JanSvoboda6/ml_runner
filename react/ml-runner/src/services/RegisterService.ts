import AuthenticationService from "../services/AuthenticationService";

function RegisterService(username: string, password: string): any
{
    return AuthenticationService.register(username, password);
};

export default RegisterService;