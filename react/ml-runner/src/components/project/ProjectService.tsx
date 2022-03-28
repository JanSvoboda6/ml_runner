import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
const API_URL = BACKEND_URL + "/api/project/saveproject";

const save = (projectName: any, firstLabel: string, secondLabel: string, firstLabelFolder: string, secondLabelFolder: string, selectedModel: any) =>
{
    const classificationLabels = [
            {
                "labelName": firstLabel,
                "folderPath": firstLabelFolder
            },
            {
                "labelName": secondLabel,
                "folderPath": secondLabelFolder
            }];
    return axios.post(API_URL, { projectName, firstLabel, secondLabel, firstLabelFolder, secondLabelFolder, selectedModel, classificationLabels}, { headers: authorizationHeader() })
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