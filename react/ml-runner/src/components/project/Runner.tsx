import axios, { AxiosResponse } from "axios";
import React, { useEffect, useState } from "react";
import RunnerService from "../../services/RunnerService";
import loadingAnimation from '../../styles/loading_graphics.gif';
import authorizationHeader from "../../services/AuthorizationHeader";

const API_URL = "http://localhost:8080/api/project";

interface Parameters
{
    gamma: number | undefined,
    c: number | undefined
}

function Runner(props: any)
{
    let intervalId: any;
    const [isFinished, setFinished] = useState(false);
    const [status, setStatus] = useState("INITIAL");
    const [firstLabelResult, setFirstLabelResult] = useState<number | undefined>(undefined);
    const [secondLabelResult, setSecondLabelResult] = useState<number | undefined>(undefined);
    const [isLoaded, setLoaded] = useState(false);
    const [parameters, setParameters] = useState<Parameters>({ gamma: undefined, c: undefined });
    const FIVE_SECONDS = 5 * 1000;

    useEffect(() =>
    {
        axios.get(API_URL + '/runner?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId, { headers: authorizationHeader() })
            .then(
                (res: AxiosResponse<any>) =>
                {
                    setLoaded(true);
                    setParameters({ gamma: res.data.gammaParameter, c: res.data.cparameter })
                    setStatus(res.data.status);
                    setFinished(res.data.finished);

                    if (res.data.finished)
                    {
                        RunnerService.getStatus(props.runnerId).then((res) => setStatus(res.data.status));
                        axios.get(API_URL + '/runner/result?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId, {headers: authorizationHeader()})
                            .then((res: AxiosResponse<any>) =>
                            {
                                setFirstLabelResult(res.data.firstLabelResult);
                                setSecondLabelResult(res.data.secondLabelResult);
                            })
                    }
                    else
                    {
                        startRecurrentRequests();
                    }
                },
                (error) =>
                {
                }
            )
    }, [])

    const startRecurrentRequests = () =>
    {
        intervalId = setInterval(isRunningFinished, FIVE_SECONDS);
    }

    const isRunningFinished = () =>
    {
        RunnerService.getStatus(props.runnerId)
            .then(
            (res) =>
                {
                    console.log(res.data.status);
                    setStatus(res.data.status);
                }
            );
        if(!isFinished)
        {
        // RunnerService.getStatus(props.runnerId).then(
        //     (res) =>{
        //         console.log(res);
        //     }
        // )
        RunnerService.isFinished(props.projectId, props.runnerId)
            .then(
                (res) =>
                {
                    setFinished(res.data.isFinished);
                    if (res.data.isFinished)
                    {
                        axios.get(API_URL + '/runner/result?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId, {headers: authorizationHeader()})
                            .then((res: AxiosResponse<any>) =>
                            {
                                setFirstLabelResult(res.data.firstLabelResult);
                                setSecondLabelResult(res.data.secondLabelResult);
                            })

                        clearInterval(intervalId);
                    }
                }
            )
        }
    }

    return (
        <div>
            <p>RUNNER ID: {props.runnerId}</p>
            <p>Gamma parameter: {parameters.gamma} </p>
            <p>C parameter: {parameters.c}</p>
            <p>Status: {status}</p>
            <div className="running-indicator">{!isFinished && <img className='loading-runner-icon' src={loadingAnimation} alt="loading_motion" />}</div>
            {isFinished && firstLabelResult !== undefined && secondLabelResult !== undefined &&
                <div>
                    <div className="text-confirm">Validation result of first label: {(firstLabelResult * 100).toFixed(2)}%</div>
                    <div className="text-confirm">Validation result of second label: {(secondLabelResult * 100).toFixed(2)}%</div>
                </div>
            }
        </div >
    )
}

export default Runner;