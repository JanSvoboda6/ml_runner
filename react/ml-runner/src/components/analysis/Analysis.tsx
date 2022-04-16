import React, {useEffect, useState} from "react";
import Heatmap from "../visualization/Heatmap";
import LegendChart from "../visualization/Legend";
import Navbar from "../navigation/Navbar";
import XyChart from "./XyChart";
import FadeIn from "react-fade-in";
import {BACKEND_URL} from "../../helpers/url";

const API_URL = BACKEND_URL + "/api/project";

function Analysis(props)
{

    const [averageValidationResult, setAverageValidationResult] = useState(0);


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
                        <div className="total">Total Runs: <div className="total-number">27</div>
                            <div className="total ">Average Validation Accuracy:</div>
                            {averageValidationResult > 0 ? <div className="total-number slow "> {averageValidationResult.toFixed(7)}</div>
                                : <div className="total"><div className="total-number"> {75.916}&#37;</div></div>}
                        </div>
                    </div>
                </div>
            </div>

            <FadeIn>
                <div className="heatmap-names">
                    <h3>Label #1 Accuracy</h3>
                    <h3>Label #2 Accuracy</h3>
                    <h3>Average Accuracy</h3>
                </div>
                <div className="heatmap-wrapper">
                    <div className="analysis-heatmap">
                        <Heatmap width={800} height={480} />
                        <LegendChart />
                    </div>
                </div>
                <div className="graph-wrapper">
                    <div className="analysis-graph">
                        <h3 className = "underlined-text">Accuracy over time</h3>
                        <XyChart width={500} height={700} />
                    </div>
                </div>
            </FadeIn>
        </div >
    )
}

export default Analysis;
