import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
import {Label} from "../../types";
const API_URL = BACKEND_URL + "/api/project/saveproject";

const save = (projectName: any, selectedModel: any, labels: Label[], ) =>
{
    let classificationLabels: any[] = [];
    labels.forEach(label => {
        classificationLabels.push({"labelName": label.name, "folderPath": label.folderPath});
    })
    return axios.post(API_URL, { projectName, selectedModel, classificationLabels}, { headers: authorizationHeader() })
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