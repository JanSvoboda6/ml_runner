import axios, { AxiosResponse } from "axios";
import { User } from "../types";

const API_URL = "http://localhost:8080/api/auth";

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
    var message: string = "";
    return axios.post(API_URL + "/register", { username, password, message })
  }
}

export default new AuthenticationService();
