import axios, { AxiosResponse } from "axios";
import React, { useEffect, useState } from "react";
import RunnerService from "../../services/RunnerService";
import loadingAnimation from '../../../public/styles/loading_graphics.gif';
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
const API_URL = BACKEND_URL + "/api/project";

interface Parameters
{
    gamma: number | undefined,
    c: number | undefined
}

function Runner(props: any)
{
    let intervalId: any;
    const [isInEndState, setEndState] = useState(false);
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
                }
            )

        RunnerService.getStatus(props.runnerId)
            .then((res) =>
            {
                setStatus(res.data.status);
                setEndState(res.data.isEndState);
                res.data.isEndState ? showResults() : startRecurrentRequests();
            });

    }, [])

    const showResults = () => {
        axios.get(API_URL + '/runner/result?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId, {headers: authorizationHeader()})
            .then((res: AxiosResponse<any>) =>
            {
                setFirstLabelResult(res.data.firstLabelResult);
                setSecondLabelResult(res.data.secondLabelResult);
            })
    }

    const startRecurrentRequests = () =>
    {
        intervalId = setInterval(isRunnerInEndState, FIVE_SECONDS);
    }

    const isRunnerInEndState = () =>
    {
        RunnerService.getStatus(props.runnerId)
            .then(
            (res) =>
                {
                    setStatus(res.data.status);
                    setEndState(res.data.isEndState)
                    if(res.data.isEndState)
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
            );
    }

    return (
        <div>
            <p>RUNNER ID: {props.runnerId}</p>
            <p>Gamma parameter: {parameters.gamma} </p>
            <p>C parameter: {parameters.c}</p>
            <p>Status: {status}</p>
            <div className="running-indicator">{!isInEndState && <img className='loading-runner-icon' src={loadingAnimation} alt="loading_motion" />}</div>
            {isInEndState && firstLabelResult !== undefined && secondLabelResult !== undefined &&
                <div>
                    <div className="text-confirm">Validation result of first label: {(firstLabelResult * 100).toFixed(2)}%</div>
                    <div className="text-confirm">Validation result of second label: {(secondLabelResult * 100).toFixed(2)}%</div>
                </div>
            }
        </div >
    )
}

export default Runner;