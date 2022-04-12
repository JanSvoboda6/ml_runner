import React, {useEffect, useState} from "react";
import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {BACKEND_URL} from "../../helpers/url";

function RunnerResult(props)
{
    const [resultText, setResultText] = useState("");

    useEffect(() =>
    {
        axios.get<any>(BACKEND_URL + '/api/project/runner/result?projectId=' + 1 + '&' + 'runnerId=' + 1, {headers: authorizationHeader()}).then(
            response => {
                setResultText(response.data.resultText);
            }
        )
    })

    return(
        <div className={"result-text"}>
            {resultText}
        </div>
    )
}

export default RunnerResult;