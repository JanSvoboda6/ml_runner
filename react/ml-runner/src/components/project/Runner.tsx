import axios, {AxiosResponse} from "axios";
import React, {useEffect, useState} from "react";
import RunnerService from "../../services/RunnerService";
import loadingAnimation from '../../styles/cube_animation.gif';
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
import {HyperParameter} from "../../types";
import moment from "moment";
import { Link } from "react-router-dom";
import Popup from "reactjs-popup";

const API_URL = BACKEND_URL + "/api/project";
const POPUP_DIMENSIONS = {"width": "700px", "minHeight": "500px"};

interface RunnerProps
{
    projectId: number,
    runnerId: number,
    selectedModel?: string,
    projectName?: string
}

/**
 * Represents a single run of a project.
 * It is symbolized by one row in the main project board.
 */
function Runner(props: RunnerProps)
{
    let intervalId: any;
    const [isInEndState, setEndState] = useState(false);
    const [status, setStatus] = useState("INITIAL");
    const [accuracy, setAccuracy] = useState<number | undefined>(undefined);
    const [isLoaded, setLoaded] = useState(false);
    const [parameters, setParameters] = useState<HyperParameter[]>([]);
    const [executedOn, setExecutedOn] = useState<number>(0);
    const FIVE_SECONDS = 5 * 1000;

    useEffect(() =>
    {
        axios.get(API_URL + '/runner?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId, { headers: authorizationHeader() })
            .then(
                (res: AxiosResponse<any>) =>
                {
                    setParameters(res.data.hyperParameters);
                    setExecutedOn(res.data.timestamp);
                    setLoaded(true);
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
                setAccuracy(res.data.accuracy);
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
                                setAccuracy(res.data.accuracy)
                            })

                        clearInterval(intervalId);
                    }
                }
            );
    }
    if(!isLoaded)
    {
        return <div>"Loading"</div>;
    }
    return (
        <div className="runner-list-table">
            <p>#{props.runnerId}</p>
            <div>
                <Popup trigger={<button className={"parameters-button"}>Parameters</button>} position="right center" modal {...{ contentStyle: POPUP_DIMENSIONS }}>
                    <div className={"p"}>Runner #{props.runnerId} - {props.selectedModel}</div>
                    <div className={"parameters-list"}>{parameters && parameters.map((parameter, index) => {
                       return <div className={"parameter-item"} key={index}>{parameter.name}: {parameter.value}</div>
                    })} </div>
                </Popup>
            </div>

            <p>{moment.unix(executedOn).format("yyyy/MM/DD HH:mm")}</p>
            <p className={status === "FINISHED" ? "text-accent" : status === "FAILED" ? "text-alert" : ""}>{status}</p>
            {!isInEndState && <img className="loading-runner-icon" src={loadingAnimation} alt="loading_motion" />}
            {isInEndState &&
                <div>
                    {accuracy !== undefined &&
                        <div className="underlined-link"><Link to={{"pathname": "/runner/result", "search": "?project=" + props.projectId +"&runner=" + props.runnerId,}}>
                            {(accuracy * 100).toFixed(2)}% </Link>
                        </div>}
                </div>
            }
    </div>
    )
}

export default Runner;