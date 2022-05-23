import axios from "axios";
import authorizationHeader from "../services/AuthorizationHeader";
import {BACKEND_URL} from "../helpers/url";

const API_URL = BACKEND_URL + "/api/docker";

/**
 * Used for contacting the API when container needs to be prepared (build a new one are start the already created one).
 */
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