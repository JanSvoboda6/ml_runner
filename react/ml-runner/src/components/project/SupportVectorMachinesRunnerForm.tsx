import React, {useEffect, useState} from "react";
import RunnerService from "../../services/RunnerService";
import {HyperParameter} from "../../types";
import ProjectService from "./ProjectService";
import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";

import {BACKEND_URL} from "../../helpers/url";

function SupportVectorMachinesRunnerForm(props: any)
{
    const [gammaParameter, setGammaParameter] = useState(100);
    const [cParameter, setCParameter] = useState(10);
    const [kernelParameter, setKernelParameter] = useState("rbf");

    const handleGammaParameterChange = (e: any) =>
    {
        e.preventDefault();
        setGammaParameter(e.target.value);
    }

    const handleCParameterChange = (e: any) =>
    {
        e.preventDefault();
        setCParameter(e.target.value);
    }

    const handleKernelParameterChange = (e: any) =>
    {
        e.preventDefault();
        setKernelParameter(e.target.value);
    }

    const handleRunButton = (e: any) =>
    {
        e.preventDefault();
        const gamma: HyperParameter = {
            name: 'gamma',
            value: gammaParameter.toString()
        }

        const c: HyperParameter = {
            name: 'c',
            value: cParameter.toString()
        }

        const kernel: HyperParameter = {
            name: 'kernel',
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
            </div>

            <div className="runner-form-block">
                <p>C parameter</p>
                <input type="text" className="input-text" data-testid="c" onChange={handleCParameterChange} placeholder="C parameter" value={cParameter} />
            </div>
            <div className="runner-form-block">
                <p>Kernel parameter</p>
                <input type="text" className="input-text" data-testid="kernel" onChange={handleKernelParameterChange} placeholder="Kernel parameter" value={kernelParameter}/>
            </div>

            <div className="runner-form-block">
                <button className="runner-form-run-button" onClick={(e) => { handleRunButton(e); props.handleRunButton() }}>Save &amp; Run</button>
            </div>
        </div>
    )
}

export default SupportVectorMachinesRunnerForm;