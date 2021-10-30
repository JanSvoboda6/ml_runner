import axios from "axios";
const API_URL = "http://localhost:8080/api/runner";

const run = (id: number) => 
{
    return axios.post<any>(API_URL + '/run', { id })
}

const stop = (id: number) =>
{
    console.log("stop" + id);
}

export default { run, stop }