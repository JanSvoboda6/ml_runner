import axios, { AxiosResponse } from "axios";
import React from "react";
import { useEffect, useState } from "react";
import ScatterGraph from "./ScatterChart";

const API_URL = "http://localhost:8080/api/project";

interface Runner
{
    projectId: number,
    runnerId: number,
    gamma: number,
    c: number,
    result: number | undefined
}

function ProjectStatistics(props)
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
                        runners.push({ projectId: runner.project.id, runnerId: runner.id, gamma: runner.gammaParameter, c: runner.cparameter, result: undefined })
                    });

                    runners.forEach(runner =>
                        axios.get(API_URL + '/runner/result?projectId=' + props.projectId + '&' + 'runnerId=' + runner.runnerId)
                            .then((res: AxiosResponse<any>) =>
                            {
                                runner.result = (res.data.firstLabelResult + res.data.secondLabelResult) / 2;
                            }))

                    setRunners(runners);
                },
                (error) =>
                {
                    setLoaded(true);
                    setErrorMessage(error.message);
                }
            )
    }, [])

    return (
        <div>
            <ScatterGraph runners={runners} />
        </div>
    )
}

export default ProjectStatistics;