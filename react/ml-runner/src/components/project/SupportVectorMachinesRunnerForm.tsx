import React, {useEffect, useState} from "react";
import RunnerService from "../../services/RunnerService";
import {HyperParameter} from "../../types";
import ProjectService from "./ProjectService";
import axios from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";

import {BACKEND_URL} from "../../helpers/url";
const API_URL = BACKEND_URL + "/api";

function SupportVectorMachinesRunnerForm(props: any)
{

    const [gammaParameter, setGammaParameter] = useState(0);
    const [cParameter, setCParameter] = useState(0);
    const [kernelParameter, setKernelParameter] = useState("");

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
            <div className="runner-form-block"> <input type="text" onChange={handleGammaParameterChange} placeholder="Gamma parameter" /> </div>
            <div className="runner-form-block"> <input type="text" onChange={handleCParameterChange} placeholder="C parameter" /> </div>
            <div className="runner-form-block"> <input type="text" onChange={handleKernelParameterChange} placeholder="Kernel" /> </div>
            <button className="runner-form-run-button" onClick={(e) => { handleRunButton(e); props.handleRunButton() }}>Save &amp; Run</button>
        </div>
    )
}

export default SupportVectorMachinesRunnerForm;