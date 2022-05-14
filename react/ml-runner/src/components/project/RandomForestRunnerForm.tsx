import React, {useState} from "react";

import {BACKEND_URL} from "../../helpers/url";
import {HyperParameter} from "../../types";
import RunnerService from "../../services/RunnerService";

function RandomForestRunnerForm(props: any)
{

    const [criterionParameter, setCriterion] = useState(0);
    const [numberOfEstimatorsParameter, setNumberOfEstimators] = useState(0);
    const [maximumDepthParameter, setMaximumDepth] = useState(0);

    const handleCriterionChange = (e: any) =>
    {
        e.preventDefault();
        setCriterion(e.target.value);
    }

    const handleNumberOfEstimatorsChange = (e: any) =>
    {
        e.preventDefault();
        setNumberOfEstimators(e.target.value);
    }

    const handleMaximumDepthChange = (e: any) =>
    {
        e.preventDefault();
        setMaximumDepth(e.target.value);
    }

    const handleRunButton = (e: any) =>
    {
        e.preventDefault();
        const criterion: HyperParameter = {
            name: 'criterion',
            value: criterionParameter.toString()
        }

        const numberOfEstimators: HyperParameter = {
            name: 'numberOfEstimators',
            value: numberOfEstimatorsParameter.toString()
        }

        const maximumDepth: HyperParameter = {
            name: 'maximumDepth',
            value: maximumDepthParameter.toString()
        }

        RunnerService.run(props.projectId, [criterion, numberOfEstimators, maximumDepth]);
        window.location.reload();
    }

    return (
        <div className="runner-form">
            <div className="runner-form-block"> <h2>{props.projectName} </h2></div>
            <div className="runner-form-block"> <input type="text" onChange={handleCriterionChange} placeholder="Criterion parameter" /> </div>
            <div className="runner-form-block"> <input type="text" onChange={handleNumberOfEstimatorsChange} placeholder="Number of estimators parameter" /> </div>
            <div className="runner-form-block"> <input type="text" onChange={handleMaximumDepthChange} placeholder="Maximum depth parameter" /> </div>
            <button className="runner-form-run-button" onClick={(e) => { handleRunButton(e); props.handleRunButton() }}>Save &amp; Run</button>
        </div>
    )
}

export default RandomForestRunnerForm;