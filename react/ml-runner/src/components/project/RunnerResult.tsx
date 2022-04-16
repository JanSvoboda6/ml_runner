import React, {useEffect, useState} from "react";
import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";
import Navbar from "../navigation/Navbar";

interface RunnerResultProps
{
    projectId: number;
    runnerId: number;
}

function RunnerResult({projectId, runnerId}: RunnerResultProps)
{
    const [resultText, setResultText] = useState("");

    useEffect(() =>
    {
        axios.get<any>(BACKEND_URL + '/api/project/runner/result?projectId=' + projectId + '&' + 'runnerId=' + runnerId, {headers: authorizationHeader()}).then(
            response => {
                setResultText(response.data.resultText);
            }
        )
    })

    return (
        <>
            <div className="result-box">
                <div className="result-text">
                    <p className="result-text-header">Result log</p>
                    <p>------------------------------------------------</p>
                    <div className="result-text-information">
                        {resultText}
                    </div>
                </div>
            </div>
        </>
    )
}

export default RunnerResult;