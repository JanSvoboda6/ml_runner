import axios from "axios";
import authorizationHeader from "../services/AuthorizationHeader";
import {BACKEND_URL} from "../helpers/url";
const API_URL = BACKEND_URL + "/api";

const run = (projectId: number, gammaParameter: number, cParameter: number) =>
{
    return axios.post<any>(API_URL + '/project/runner/run', { projectId: projectId, gammaParameter: gammaParameter, cParameter: cParameter }, { headers: authorizationHeader() });
}

const stop = (projectId: number, runnerId: number) =>
{
    //not implemented
}

const isFinished = (projectId: number, runnerId: number) =>
{
    return axios.post<any>(API_URL + '/project/runner/finished', { projectId: projectId, runnerId: runnerId }, { headers: authorizationHeader() });
}

const getStatus = (runnerId: number) =>
{
    return axios.get<any>(API_URL + '/project/runner/status?runnerId=' + runnerId, { headers: authorizationHeader() });
}


export default { run, stop, isFinished, getStatus }