import axios from "axios";
const API_URL = "http://localhost:8080/api";

const run = (projectId: number, gammaParameter: number, cParameter: number) =>
{
    return axios.post<any>(API_URL + '/project/runner/run', { projectId: projectId, gammaParameter: gammaParameter, cParameter: cParameter });
}

const stop = (projectId: number, runnerId: number) =>
{
    //not implemented
}

const isFinished = (projectId: number, runnerId: number) =>
{
    return axios.post<any>(API_URL + '/project/runner/finished', { projectId: projectId, runnerId: runnerId });
}

export default { run, stop, isFinished }