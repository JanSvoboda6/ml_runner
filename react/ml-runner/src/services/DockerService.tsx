import axios from "axios";
import { FileInformation } from "../types";
import authorizationHeader from "../services/AuthorizationHeader";
import {BACKEND_URL} from "../helpers/url";
const API_URL = BACKEND_URL + "/api/docker";

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