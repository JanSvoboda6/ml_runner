import React, {useEffect, useState} from "react";
import RunnerService from "../../services/RunnerService";

interface RunnerResultProps
{
    projectId: number;
    runnerId: number;
}

/**
 * Component providing logs of the runner.
 */
function RunnerResult({projectId, runnerId}: RunnerResultProps)
{
    const [resultText, setResultText] = useState("");

    useEffect(() =>
    {
        RunnerService.getResult(projectId, runnerId).then(
            response => {
                setResultText(response.data.resultText);
            }
        )
    })

    return (
        <>
            <div className="result-box">
                <div className="result-text">
                    <p className="result-text-header">Runner #{runnerId} - Result Log</p>
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