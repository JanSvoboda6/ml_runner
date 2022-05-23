import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
import {Label} from "../../helpers/types";
const API_URL = BACKEND_URL + "/api/project/saveproject";

/**
 * Used for saving a project.
 */
const save = (projectName: string, selectedModel: string, labels: Label[], ) =>
{
    let classificationLabels: any[] = [];
    labels.forEach(label => {
        classificationLabels.push({"labelName": label.name, "folderPath": label.folderPath});
    })
    return axios.post(API_URL, { projectName, selectedModel, classificationLabels}, { headers: authorizationHeader()});
}

export default { save }