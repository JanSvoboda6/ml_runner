import axios, { AxiosResponse } from "axios";
import React from "react";
import { useEffect, useState } from "react";
import ScatterGraph from "../visualization/ScatterChart";
import authorizationHeader from "../../services/AuthorizationHeader";
import Heatmap from "../visualization/Heatmap";
import { Legend } from "@visx/legend";
import Example from "../visualization/Legend";
import Navbar from "../navigation/Navbar";
import XyChart from "./XyChart";
import FadeIn from "react-fade-in";
import SimpleGraph from "./SimpleGraph";


const API_URL = "http://localhost:8080/api/project";

interface Runner
{
    projectId: number,
    runnerId: number,
    gamma: number,
    c: number,
    result: number | undefined
}

function Analysis(props)
{
    const [isLoaded, setLoaded] = useState(false);
    const [runners, setRunners] = useState<Runner[]>([]);
    const [totalRuns, setTotalRuns] = useState(0);
    const [averageValidationResult, setAverageValidationResult] = useState(0);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() =>
    {
 
    }, [])

    function updateDisplayedValue(value: any)
    {
        console.log(value);
    }

    return (
        <div>
            <Navbar start="start-at-projects" />
            <div className="summary-list">
                <div className="summary-list-item">
                    <div className="total-list">
                        <div className="total">Total Runs: <div className="total-number">{totalRuns}</div>
                            <div className="total ">Average Validation Result:</div>
                            {averageValidationResult > 0 ? <div className="total-number slow "> {averageValidationResult.toFixed(7)}</div>
                                : <div className="total">Average Validation Result: <div className="total-number"> {averageValidationResult}</div></div>}
                        </div>
                    </div>
                </div>
            </div>
            <FadeIn>
            <SimpleGraph />
            <div className="heatmap-wrapper">
                <div className="analysis-heatmap">
                    <Heatmap width={800} height={480}/>
                    <Example />
                </div>
                <div className="analysis-heatmap">
                    <Heatmap width={800} height={480} />
                    <Example />
                </div>
            </div>
            <div className="graph-wrapper">
                <div className="analysis-graph">
                    <XyChart width={500} height={700} />
                </div>
            </div>
            </FadeIn>
        </div >
    )
}

export default Analysis;
