import axios from "axios";
import { FileInformation } from "../types";
import authorizationHeader from "../services/AuthorizationHeader";
const API_URL = "http://localhost:8080/api/docker";

const prepareContainer = () =>
{
    return axios.get(
        API_URL,
        {
            headers: authorizationHeader(),
        }
    );
};

export default { prepareContainer }