import React, {useState} from "react";
import RunnerService from "../../services/RunnerService";
import {HyperParameter} from "../../helpers/types";
import ParameterInfoTooltip from "./ParameterInfoToolTip";

interface RunnerForm
{
    projectId: number,
    projectName: string,
    handleRunButton: Function
}

/**
 * Used for submitting a Support Vector Machines runner.
 */
function SupportVectorMachinesRunnerForm(props: RunnerForm)
{
    const [gammaParameter, setGammaParameter] = useState("100");
    const [cParameter, setCParameter] = useState("10");
    const [kernelParameter, setKernelParameter] = useState("rbf");

    const handleGammaParameterChange = (e: { target: { value: string; }; }) =>
    {
        setGammaParameter(e.target.value);
    }

    const handleCParameterChange = (e: { target: { value: string; }; }) =>
    {
        setCParameter(e.target.value);
    }

    const handleKernelParameterChange = (e: { target: { value: string; }; }) =>
    {
        setKernelParameter(e.target.value);
    }

    const handleRunButton = (e: any) =>
    {
        e.preventDefault();
        const gamma: HyperParameter = {
            name: "gamma",
            value: gammaParameter
        }

        const c: HyperParameter = {
            name: "c",
            value: cParameter
        }

        const kernel: HyperParameter = {
            name: "kernel",
            value: kernelParameter
        }

        RunnerService.run(props.projectId, [gamma, c, kernel]);
        window.location.reload();
    }

    return (
        <div className="runner-form">
            <div className="runner-form-block"> <h2>{props.projectName} </h2></div>
            <div className="runner-form-block">
                <p>Gamma parameter</p>
                <input type="input-text" className="input-text" data-testid="gamma" onChange={handleGammaParameterChange} placeholder="Gamma parameter" value={gammaParameter}/>
                <ParameterInfoTooltip textContent={"Choose positive float value."} />
            </div>

            <div className="runner-form-block">
                <p>C parameter</p>
                <input type="text" className="input-text" data-testid="c" onChange={handleCParameterChange} placeholder="C parameter" value={cParameter} />
                <ParameterInfoTooltip textContent={"Choose positive float value."} />
            </div>
            <div className="runner-form-block">
                <p>Kernel parameter</p>
                <input type="text" className="input-text" data-testid="kernel" onChange={handleKernelParameterChange} placeholder="Kernel parameter" value={kernelParameter}/>
                <ParameterInfoTooltip textContent={"Choose one of the values: rbf, poly, linear."} />
            </div>

            <div className="runner-form-block">
                <button className="runner-form-run-button" onClick={(e) => { handleRunButton(e); props.handleRunButton() }}>Save &amp; Run</button>
            </div>
        </div>
    )
}

export default SupportVectorMachinesRunnerForm;