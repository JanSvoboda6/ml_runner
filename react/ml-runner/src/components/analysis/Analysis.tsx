import React, {useEffect, useState} from "react";
import Heatmap from "../visualization/Heatmap";
import LegendChart from "../visualization/Legend";
import Navbar from "../navigation/Navbar";
import XyChart from "./XyChart";
import FadeIn from "react-fade-in";
import {BACKEND_URL} from "../../helpers/url";
import HeatmapDataProvider from "./HeatmapDataProvider";
import RunnerService from "../../services/RunnerService";
import axios, {AxiosResponse} from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {HyperParameter} from "../../types";
import runner from "../project/Runner";
import {useLocation} from "react-router";
import queryString from "query-string";
import SimpleGraph from "./SimpleGraph";
import SimpleChart from "./SimpleChart";

const API_URL = BACKEND_URL + "/api/project";

export type Bin = {
    count: number
}

export type Bins = {
    bins: Bin[]
}

type IndividualResult = {
    xAxisValue: string,
    yAxisValue: string,
    result: number
}

type Runner = {
    id: number,
    accuracy: number,
    hyperParameters: HyperParameter[]
}

function Analysis(props)
{
    const location = useLocation();
    const parameters = queryString.parse(location.search);
    const projectId = parseInt(parameters.projectId);

    const [runners, setRunners] = useState<Runner[]>([]);
    const [averageValidationResult, setAverageValidationResult] = useState(0);
    const [bins, setBins] = useState<Bins[]>([]);
    const [isLoaded, setLoaded] = useState(false);
    const [isInErrorState, setErrorState] = useState(false);
    const [firstHyperParameter, setFirstHyperParameter] = useState("");
    const [secondHyperParameter, setSecondHyperParameter] = useState("");
    const [hyperParameterSelector, setHyperParameterSelector] = useState<string[]>([]);

    useEffect(() =>
    {
        axios.get(API_URL + "/runners?projectId=" + projectId, {headers: authorizationHeader()})
            .then(
                (res: AxiosResponse<any>) =>
                {
                    constructRunnersWithResult(res.data).then((runnersResult) => {

                        let hyperParametersName: string[] = [];
                        runnersResult[0].hyperParameters.forEach(parameter => hyperParametersName.push(parameter.name));
                        console.log(hyperParametersName);
                        setHyperParameterSelector(hyperParametersName);
                        setFirstHyperParameter(hyperParametersName[0]);
                        setSecondHyperParameter(hyperParametersName[1]);
                        setRunners(runnersResult);
                        setLoaded(true);
                    });
                },
                (error) =>
                {
                    setErrorState(true);
                    setLoaded(true);
                }
            )
    }, [])

    async function constructRunnersWithResult(runners: any[])
    {
        const runnersResult: Array<Runner> = [];
        for(let i = 0; i < runners.length; i++)
        {
            await axios.get(API_URL + '/runner/result?projectId=' + projectId + '&' + 'runnerId=' + runners[i].id, {headers: authorizationHeader()})
                .then((res: AxiosResponse<any>) =>
                {
                    runnersResult.push({id: runners[i].id, accuracy: res.data.accuracy, hyperParameters: runners[i].hyperParameters})
                })
        }
        return runnersResult;
        // return [
        //     {id: 1, accuracy: 0.5, hyperParameters: [{name: "gamma", value: "1"}, {name: "c", value:"1"}]},
        //     {id: 2, accuracy: 0.6, hyperParameters: [{name: "gamma", value: "2"}, {name: "c", value:"3"}]},
        //     {id: 3, accuracy: 0.7, hyperParameters: [{name: "gamma", value: "4"}, {name: "c", value:"2"}]},
        //     {id: 4, accuracy: 0.8, hyperParameters: [{name: "gamma", value: "3"}, {name: "c", value:"3"}]},
        //     // {id: 4, accuracy: 0.8, hyperParameters: [{name: "gamma", value: "0.000002"}, {name: "c", value:"2"}]},
        //     // {id: 4, accuracy: 0.8, hyperParameters: [{name: "gamma", value: "3"}, {name: "c", value:"0.000002"}]},
        //     // {id: 5, accuracy: 0.9, hyperParameters: [{name: "gamma", value: "3"}, {name: "c", value:"1"}]},
        //     // {id: 5, accuracy: 0.9, hyperParameters: [{name: "gamma", value: "4"}, {name: "c", value:"1"}]},
        //     // {id: 5, accuracy: 0.9, hyperParameters: [{name: "gamma", value: "4"}, {name: "c", value:"4"}]},
        // ];
    }

    function updateDisplayedValue(value: any)
    {
    }

    const getHyperParameterValues = (runners: Runner[], hyperParameter) => {
        let hyperParameterValues: Array<string> = [];

        runners.forEach(runner => {
            runner.hyperParameters.forEach(parameter => {
                if (parameter.name === hyperParameter)
                {
                    if (!hyperParameterValues.includes(parameter.value))
                    {
                        hyperParameterValues.push(parameter.value);
                    }
                }
            });
        });
        return hyperParameterValues.sort(new Intl.Collator(undefined, {numeric: true, sensitivity: 'base'}).compare);
    }

    const addToResults = (resultToAdd:IndividualResult, tickValuesX: Array<string>, tickValuesY: Array<string>, results) => {
        results[tickValuesX.indexOf(resultToAdd.xAxisValue)][tickValuesY.indexOf(resultToAdd.yAxisValue)] = resultToAdd.result;
        return results;
    }

    const fillResults = (results: number[][], runners: Runner[], xHyperParameter: string, yHyperParameter: string, tickValuesX: Array<string>, tickValuesY: Array<string>) =>
    {
        runners.forEach(runner => {
            const result = runner.accuracy;
            let xAxisValue = "";
            let yAxisValue = "";
            runner.hyperParameters.forEach(parameter => {
                if (parameter.name === xHyperParameter)
                {
                    xAxisValue = parameter.value;
                } else if (parameter.name === yHyperParameter)
                {
                    yAxisValue = parameter.value;
                }
            })
            results = addToResults({xAxisValue, yAxisValue, result}, tickValuesX, tickValuesY, results);
        })
        return results;
    };


    if(isInErrorState)
    {
        return <p>ERROR!</p>;
    }

    const handleFirstHyperParameterSelection = (event: any) =>{
        setFirstHyperParameter(event.target.value);
    }

    const handleSecondHyperParameterSelection = (event: any) =>{
        setSecondHyperParameter(event.target.value);
    }


    if(isLoaded)
    {

        console.log(firstHyperParameter);
        const tickValuesX = getHyperParameterValues(runners, firstHyperParameter);
        const tickValuesY = getHyperParameterValues(runners,  secondHyperParameter);

        let results = Array(tickValuesX.length).fill(0).map(() => Array(tickValuesY.length));

        results = fillResults(results, runners, firstHyperParameter, secondHyperParameter, tickValuesX, tickValuesY);

        const generatedBins = HeatmapDataProvider.constructBins(results);

        return (
            <div>
                <Navbar start="start-at-projects"/>

                <div className="summary-list">
                    <div className="summary-list-item">
                        <div className="total-list">
                            <div className="total">Total Runs: <div className="total-number">27</div>
                                <div className="total ">Average Validation Accuracy:</div>
                                {averageValidationResult > 0 ?
                                    <div className="total-number slow "> {averageValidationResult.toFixed(7)}</div>
                                    : <div className="total">
                                        <div className="total-number"> {75.916}&#37;</div>
                                    </div>}
                            </div>
                        </div>
                    </div>
                </div>

                <FadeIn>
                    {/*<div className="heatmap-names">*/}
                    {/*    <h3>Label #1 Accuracy</h3>*/}
                    {/*    <h3>Label #2 Accuracy</h3>*/}
                    {/*    <h3>Average Accuracy</h3>*/}
                    {/*</div>*/}
                    <div className="heatmap-wrapper">
                        <div className="hyper-parameter-selector">
                            <p>X axis: </p>
                            <select name="model-select" value={firstHyperParameter} onChange={handleFirstHyperParameterSelection}>
                                {hyperParameterSelector.map((parameter, key) => {
                                    return <option key={key} value={parameter} disabled={parameter === secondHyperParameter}>{parameter}</option>;
                                })}
                            </select>
                        </div>
                        <div className="hyper-parameter-selector">
                            <p>Y axis: </p>
                            <select name="hyperParameters2" value={secondHyperParameter} onChange={handleSecondHyperParameterSelection}>
                                {hyperParameterSelector.map((parameter, key) => {
                                    return <option key={key} value={parameter} disabled={parameter === firstHyperParameter}>{parameter}</option>;
                                })}
                            </select>
                        </div>
                        <div className="analysis-heatmap">
                            <Heatmap bins={generatedBins} tickValuesX={tickValuesX} tickValuesY={tickValuesY}
                                     width={600} height={500} xAxisLabel={firstHyperParameter + " [-]"}
                                     yAxisLabel={secondHyperParameter + " [-]"}/>
                            <LegendChart/>
                        </div>
                    </div>

                    <div className="graph-wrapper">
                        <div className="analysis-graph">
                            <h3 className="underlined-text">Accuracy over time</h3>
                            <SimpleChart/>
                        </div>
                    </div>

                    {/*<div className="graph-wrapper">*/}
                    {/*    <div className="analysis-graph">*/}
                    {/*        <h3 className="underlined-text">Accuracy over time</h3>*/}
                    {/*        <XyChart width={500} height={700}/>*/}
                    {/*    </div>*/}
                    {/*</div>*/}
                </FadeIn>
            </div>
        )
    }
    return <p>LOADING</p>;
}

export default Analysis;
