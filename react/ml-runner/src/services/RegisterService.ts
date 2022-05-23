import AuthenticationService from "../services/AuthenticationService";

/**
 * Wrapper for AuthenticationService#register, if any action is needed to be executed while registering, this is a place
 * where the code should be placed.
 */
const register = (username: string, password: string): any =>
{
    return AuthenticationService.register(username, password);
};

export default {register};