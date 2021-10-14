import axios, { AxiosResponse } from "axios";
import { User } from "../types";

const API_URL = "http://localhost:8080/api/auth";

class AuthService
{
  async login(username: string, password: string) 
  {
    const response: AxiosResponse<User> = await axios.post(API_URL + "/signin", { username, password });

    if (response.data.accessToken)
    {
      localStorage.setItem("user", JSON.stringify(response.data));
    }

    return response.data;
  }

  logout()
  {
    localStorage.removeItem("user")
  }

  register(username: string, email: string, password: string)
  {
    var message: string = "";
    return axios.post(API_URL + "/signup", { username, email, password, message })
  }

}

export default new AuthService();
