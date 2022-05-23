import axios from "axios";
import authorizationHeader from "../services/AuthorizationHeader";
import {BACKEND_URL} from "../helpers/url";
import {HyperParameter} from "../helpers/types";
const API_URL = BACKEND_URL + "/api";

/**
 * Providing the access to the API for exeution of a runner, monitoring its status and getting results.
 */
const run = (projectId: number, hyperParameters: HyperParameter[]) =>
{
    return axios.post<any>(API_URL + '/project/runner/run', { projectId: projectId, hyperParameters: hyperParameters }, { headers: authorizationHeader() });
}

const getStatus = (runnerId: number) =>
{
    return axios.get<any>(API_URL + '/project/runner/status?runnerId=' + runnerId, { headers: authorizationHeader() });
}

const getResult = (projectId: number, runnerId: number) => {
    return axios.get<any>(BACKEND_URL + '/api/project/runner/result?projectId=' + projectId + '&' + 'runnerId=' + runnerId, {headers: authorizationHeader()})
}

export default { run, getStatus, getResult }