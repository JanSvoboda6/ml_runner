import axios, { AxiosResponse } from "axios";
import React from "react";
import { useEffect, useState } from "react";
import ScatterGraph from "./ScatterChart";
import authorizationHeader from "../../services/AuthorizationHeader";
import Heatmap from "./Heatmap";
import { Legend } from "@visx/legend";
import Example from "./Legend";
import {BACKEND_URL} from "../../helpers/url";
const API_URL = BACKEND_URL + "/api/project";

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
    const [totalRuns, setTotalRuns] = useState(0);
    const [averageValidationResult, setAverageValidationResult] = useState(0);
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

                    let cumulativeResult = 0;
                    runners.forEach((runner, index) =>
                        axios.get(API_URL + '/runner/result?projectId=' + props.projectId + '&' + 'runnerId=' + runner.runnerId, { headers: authorizationHeader() })
                            .then((res: AxiosResponse<any>) =>
                            {
                                runners[index]['result'] = (res.data.firstLabelResult + res.data.secondLabelResult) / 2;
                                cumulativeResult += (res.data.firstLabelResult + res.data.secondLabelResult) / 2;
                                setAverageValidationResult(cumulativeResult / runners.length);
                            }));

                    setRunners(runners);
                    setTotalRuns(runners.length);
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
            <div className="summary-list">
                <div className="summary-list-item">
                    <div className="total-list">
                        <div className="total">Total Runs: <div className="total-number">{totalRuns}</div>
                            <div className="total ">Average Validation Result:</div>
                            {averageValidationResult > 0 ? <div className="total-number slow "> {averageValidationResult.toFixed(7)}</div>
                                : <div className="total">Average Validation Result: <div className="total-number"> {averageValidationResult}</div></div>}
                        </div>
                    </div>
                </div>
            </div>
            <div className="heatmap-wrapper">
                <Example/>
                <Heatmap width={800} height={480} />
            </div>
            <ScatterGraph runners={runners} />
        </div >
    )
}

export default ProjectStatistics;