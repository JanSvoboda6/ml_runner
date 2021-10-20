import AuthenticationService from "../services/AuthenticationService";

function RegisterService(username: string, email: string, password: string): any
{
    return AuthenticationService.register(username, email, password);
};

export default RegisterService;