import axios, { AxiosResponse } from "axios";
import React, { useEffect, useState } from "react";
import RunnerService from "../../services/RunnerService";
import loadingIcon from '../../styles/loading_icon.svg';

const API_URL = "http://localhost:8080/api/project";

interface Parameters
{
    gamma: number | undefined,
    c: number | undefined
}

function Runner(props: any)
{
    let intervalId: any;
    let isFinishedControlForIntervalHook = undefined;
    const [isFinished, setFinished] = useState(false);
    const [firstLabelResult, setFirstLabelResult] = useState<number | undefined>(undefined);
    const [secondLabelResult, setSecondLabelResult] = useState<number | undefined>(undefined);
    const [isLoaded, setLoaded] = useState(false);
    const [parameters, setParameters] = useState<Parameters>({ gamma: undefined, c: undefined });

    useEffect(() =>
    {
        axios.get(API_URL + '/runner?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId)
            .then(
                (res: AxiosResponse<any>) =>
                {
                    setLoaded(true);
                    setParameters({ gamma: res.data.gammaParameter, c: res.data.cparameter })
                    setFinished(res.data.finished);
                    isFinishedControlForIntervalHook = res.data.finished;

                    if (res.data.finished)
                    {
                        axios.get(API_URL + '/runner/result?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId)
                            .then((res: AxiosResponse<any>) =>
                            {
                                setFirstLabelResult(res.data.firstLabelResult);
                                setSecondLabelResult(res.data.secondLabelResult);
                            })
                    }

                    if (!res.data.finished)
                    {
                        startReccurentRequests();
                    }
                },
                (error) =>
                {
                }
            )


    }, [])

    const startReccurentRequests = () =>
    {
        intervalId = setInterval(isRunningFinished, 1000);
    }

    const isRunningFinished = () =>
    {
        console.log(isFinishedControlForIntervalHook);
        console.log(intervalId);

        RunnerService.isFinished(props.projectId, props.runnerId)
            .then(
                (res) =>
                {
                    console.log(res.data.isFinished);
                    console.log(res.data.firstLabelResult);
                    console.log(res.data.secondLabelResult);
                    setFinished(res.data.isFinished);
                    if (res.data.isFinished)
                    {
                        clearInterval(intervalId);
                        setFirstLabelResult(res.data.firstLabelResult);
                        setSecondLabelResult(res.data.secondLabelResult);
                    }
                },
                (error) =>
                {
                    console.log("PROBLEM");
                }
            )
    }


    return (
        <div>
            <p> RUNNER ID:{props.runnerId}</p>
            <p>Gamma parameter: {parameters.gamma} </p>
            <p>C parameter: {parameters.c}</p>
            <p>IS FINISHED: {isFinished ? "TRUE" : "FALSE"}</p>
            <div className="running-indicator">{!isFinished && <img className='loading-runner-icon' src={loadingIcon} alt="loading_icon" />}</div>
            {isFinished && firstLabelResult !== undefined && secondLabelResult !== undefined &&
                <div>
                    <div>Validation result of first label: {(firstLabelResult * 100).toFixed(2)}%</div>
                    <div>Validation result of second label: {(secondLabelResult * 100).toFixed(2)}%</div>
                </div>
            }
        </div >
    )
}

export default Runner;