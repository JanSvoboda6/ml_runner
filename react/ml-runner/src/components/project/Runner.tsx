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
const POPUP_DIMENSIONS = {"width": "700px", "min-height": "700px"};

function Runner(props: any)
{
    let intervalId: any;
    const [isInEndState, setEndState] = useState(false);
    const [status, setStatus] = useState("INITIAL");
    const [accuracy, setAccuracy] = useState<number | undefined>(undefined);
    const [isLoaded, setLoaded] = useState(false);
    const [parameters, setParameters] = useState<HyperParameter[]>([]);
    const FIVE_SECONDS = 5 * 1000;

    useEffect(() =>
    {
        axios.get(API_URL + '/runner?projectId=' + props.projectId + '&' + 'runnerId=' + props.runnerId, { headers: authorizationHeader() })
            .then(
                (res: AxiosResponse<any>) =>
                {
                    setLoaded(true);
                    setParameters(res.data.hyperParameters)
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
            <Popup trigger={<button className={"parameters-button"}>Parameters</button>} position="right center" modal {...{ contentStyle: POPUP_DIMENSIONS }}>
                Runner #{props.runnerId} - {props.selectedModel}
                <div className={"parameters-list"}>{parameters && parameters.map((parameter, index) => {
                   return <div className={"parameter-item"} key={index}>{parameter.name}: {parameter.value}</div>
                })} </div>
            </Popup>

            <p>{moment().format("DD/MM/YYYY")}</p>
            <p className={status == 'FINISHED' ? "text-confirm" : ""}>{status}</p>
            {!isInEndState && <img className='loading-runner-icon' src={loadingAnimation} alt="loading_motion" />}
            {isInEndState &&
                <div>
                    {accuracy !== undefined &&
                        <div className='underlined-link'><Link to={{'pathname': '/runner/result', 'search': '?project=' + props.projectId +'&runner=' + props.runnerId,}}>
                            {(accuracy * 100).toFixed(2)}% </Link>
                        </div>}
                </div>
            }
    </div>
    )
}

export default Runner;