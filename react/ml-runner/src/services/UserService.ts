import axios from 'axios';
import authenticationHeader from './AuthenticationHeader';

const API_URL = 'http://localhost:8080/api/test/';

class UserService
{
    getPublicContent()
    {
        return axios.get(API_URL + 'all');
    }

    getUserContent()
    {
        return axios.get(API_URL + 'user', { headers: authenticationHeader() });
    }
}

export default new UserService();