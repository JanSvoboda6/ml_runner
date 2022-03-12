import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
const API_URL = "http://localhost:8080/api/project/saveproject";

const save = (projectName: any, firstLabel: string, secondLabel: string, firstLabelFolder: string, secondLabelFolder: string, selectedModel: any) =>
{
    return axios.post(API_URL, { projectName, firstLabel, secondLabel, firstLabelFolder, secondLabelFolder, selectedModel }, { headers: authorizationHeader() })
        .catch(error =>
        {
            var message = "";
            if (error && error.response && error.response.data.message)
            {
                message = error.response.data.message;
            }
            else if (error.message)
            {
                message = error.message;
            }
            else if (error.toString())
            {
                message = error.toString();
            }
        });
}

export default { save }