import React, {useState} from "react";
import {HyperParameter} from "../../helpers/types";
import RunnerService from "../../services/RunnerService";
import ParameterInfoTooltip from "./ParameterInfoToolTip";

interface RunnerForm
{
    projectId: number,
    projectName: string,
    handleRunButton: Function
}

/**
 * Used for submitting a Random Forest runner.
 */
function RandomForestRunnerForm(props: RunnerForm)
{
    const [criterionParameter, setCriterion] = useState("gini");
    const [numberOfEstimatorsParameter, setNumberOfEstimators] = useState("100");
    const [maximumDepthParameter, setMaximumDepth] = useState("100");

    const handleCriterionChange = (e: { target: { value: string; }; }) =>
    {
        setCriterion(e.target.value);
    }

    const handleNumberOfEstimatorsChange = (e: { target: { value: string; }; }) =>
    {
        setNumberOfEstimators(e.target.value);
    }

    const handleMaximumDepthChange = (e: { target: { value: string; }; }) =>
    {
        setMaximumDepth(e.target.value);
    }

    const handleRunButton = () =>
    {
        const criterion: HyperParameter = {
            name: "criterion",
            value: criterionParameter
        }

        const numberOfEstimators: HyperParameter = {
            name: "numberOfEstimators",
            value: numberOfEstimatorsParameter
        }

        const maximumDepth: HyperParameter = {
            name: "maximumDepth",
            value: maximumDepthParameter
        }

        RunnerService.run(props.projectId, [criterion, numberOfEstimators, maximumDepth]);
        window.location.reload();
    }

    return (
        <div className="runner-form">
            <div className="runner-form-block"> <h2>{props.projectName} </h2></div>
            <div className="runner-form-block">
                <p>Criterion parameter</p>
                <input type="text" className="input-text" onChange={handleCriterionChange} placeholder="Criterion parameter" value={criterionParameter}/>
                <ParameterInfoTooltip textContent={"Choose one of the values: gini, entropy, log_loss."} />
            </div>
            <div className="runner-form-block">
                <p>Number of estimators (trees)</p>
                <input type="text" className="input-text" onChange={handleNumberOfEstimatorsChange} placeholder="Number of estimators" value={numberOfEstimatorsParameter}/>
                <ParameterInfoTooltip textContent={"Choose integer value."} />
            </div>
            <div className="runner-form-block">
                <p>Maximum depth parameter</p>
                <input type="text" className="input-text" onChange={handleMaximumDepthChange} placeholder="Maximum depth parameter" value={maximumDepthParameter}/>
                <ParameterInfoTooltip textContent={"Choose integer value."} />
            </div>
            <div className="runner-form-block">
                <button className="runner-form-run-button" onClick={(e) => { handleRunButton(); props.handleRunButton() }}>Save &amp; Run</button>
            </div>
        </div>
    )
}

export default RandomForestRunnerForm;