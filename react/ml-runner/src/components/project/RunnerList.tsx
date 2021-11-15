import axios, { AxiosResponse } from "axios";
import React from "react";
import { useEffect, useState } from "react";
import Runner from "./Runner";

const API_URL = "http://localhost:8080/api/project";

interface Runner
{
    projectId: number,
    runnerId: number
}

function RunnerList(props: any)
{
    const [isLoaded, setLoaded] = useState(false);
    const [runners, setRunners] = useState<Runner[]>([]);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() =>
    {
        console.log('RunnerList');
        axios.get(API_URL + "/runners?projectId=" + props.projectId)
            .then(
                (res: AxiosResponse<any>) =>
                {
                    setLoaded(true);
                    const runners: Array<Runner> = [];
                    res.data.forEach(runner =>
                    {
                        runners.push({ projectId: runner.project.id, runnerId: runner.id })
                    });
                    setRunners(runners);
                    console.log(res.data);
                },
                (error) =>
                {
                    setLoaded(true);
                    setErrorMessage(error.message);
                }
            )
    }, [])

    if (!isLoaded)
    {
        return <div> Loading...</div>
    }

    //TODO Jan: generate proper runner key
    return (
        <div>
            <ul className="runner-list-inside">
                {runners.map(runner => (
                    <li key={runner.runnerId} className="runner-item">
                        <Runner projectName={props.projectName} projectId={runner.projectId} runnerId={runner.runnerId} />
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default RunnerList;