import axios, { AxiosResponse } from "axios";
import { User } from "../types";
import {BACKEND_URL} from "../helpers/url";
const API_URL = BACKEND_URL + "/api/auth";

class AuthenticationService
{
  async login(username: string, password: string) 
  {
    const response: AxiosResponse<User> = await axios.post(API_URL + "/login", { username, password });

    if (response.data.accessToken)
    {
      localStorage.setItem("user", JSON.stringify(response.data));
    }
    return response.data;
  }

  logout()
  {
    localStorage.removeItem("user");
  }

  register(username: string, password: string)
  {
    return axios.post(API_URL + "/register", { username, password })
  }
}

export default new AuthenticationService();
