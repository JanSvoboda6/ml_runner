import React, {useEffect, useState} from "react";
import Heatmap from "./visualization/Heatmap";
import LegendChart from "./visualization/Legend";
import Navbar from "../navigation/Navbar";
import FadeIn from "react-fade-in";
import {BACKEND_URL} from "../../helpers/url";
import HeatmapDataProvider from "./HeatmapDataProvider";
import axios, {AxiosResponse} from "axios";
import authorizationHeader from "../../services/AuthorizationHeader";
import {HyperParameter} from "../../types";
import {useLocation} from "react-router";
import queryString from "query-string";
import Chart, {Data} from "./visualization/Chart";
import fromExponential from 'from-exponential';
import HelperBox from "../navigation/HelperBox";

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
                    if(res.data.length < 2) {
                        setErrorState(true);
                        return;
                    }
                    constructRunnersWithResult(res.data).then((runnersResult) => {

                        let hyperParametersName: string[] = [];
                        runnersResult[0].hyperParameters.forEach(parameter => hyperParametersName.push(parameter.name));
                        setHyperParameterSelector(hyperParametersName);
                        setFirstHyperParameter(hyperParametersName[0]);
                        setSecondHyperParameter(hyperParametersName[1]);
                        setRunners(runnersResult);

                        let averageAccuracy = 0;
                        runnersResult.forEach(result => {
                            averageAccuracy += result.accuracy;
                        })
                        setAverageValidationResult(averageAccuracy/runnersResult.length);

                        setLoaded(true);
                    });
                },
                (error) =>
                {
                    setErrorState(true);
                }
            )
    }, [])

    async function constructRunnersWithResult(runners: any[])
    {
        const runnersResult: Array<Runner> = [];
        for(let i = 0; i < runners.length; i++)
        {
            if(runners[i].status === "FINISHED")
            {
                await axios.get(API_URL + '/runner/result?projectId=' + projectId + '&' + 'runnerId=' + runners[i].id, {headers: authorizationHeader()})
                    .then((res: AxiosResponse<any>) =>
                    {
                        runnersResult.push({
                            id: runners[i].id,
                            accuracy: res.data.accuracy,
                            hyperParameters: runners[i].hyperParameters
                        })
                    })
            }
        }
        return runnersResult;
        // return [
        //     {id: 1, accuracy: 0.685, hyperParameters: [{name: "gamma", value: "0.1"}, {name: "c", value:"0.1"}, {name: "kernel", value:"rbf"}]},
        //     {id: 2, accuracy: 0.889, hyperParameters: [{name: "gamma", value: "0.1"}, {name: "c", value:"1"}, {name: "kernel", value:"rbf"}]},
        //     {id: 3, accuracy: 0.6786, hyperParameters: [{name: "gamma", value: "0.1"}, {name: "c", value:"10"}, {name: "kernel", value:"rbf"}]},
        //     {id: 4, accuracy: 0.985, hyperParameters: [{name: "gamma", value: "0.1"}, {name: "c", value:"100"}, {name: "kernel", value:"rbf"}]},
        //     {id: 5, accuracy: 0.456, hyperParameters: [{name: "gamma", value: "0.1"}, {name: "c", value:"1000"}, {name: "kernel", value:"rbf"}]},
        //     {id: 6, accuracy: 0.548, hyperParameters: [{name: "gamma", value: "1"}, {name: "c", value:"0.1"},{name: "kernel", value:"linear"}]},
        //     {id: 7, accuracy: 0.893, hyperParameters: [{name: "gamma", value: "1"}, {name: "c", value:"1"}, {name: "kernel", value:"linear"}]},
        //     {id: 8, accuracy: 0.675, hyperParameters: [{name: "gamma", value: "1"}, {name: "c", value:"10"}, {name: "kernel", value:"poly"}]},
        //     {id: 9, accuracy: 0.912, hyperParameters: [{name: "gamma", value: "1"}, {name: "c", value:"100"}, {name: "kernel", value:"poly"}]},
        //     {id: 10, accuracy: 0.8768, hyperParameters: [{name: "gamma", value: "1"}, {name: "c", value:"1000"}, {name: "kernel", value:"poly"}]},
        //     {id: 11, accuracy: 0.866, hyperParameters: [{name: "gamma", value: "10"}, {name: "c", value:"0.1"}, {name: "kernel", value:"linear"}]},
        //     {id: 12, accuracy: 0.756, hyperParameters: [{name: "gamma", value: "10"}, {name: "c", value:"1"}, {name: "kernel", value:"linear"}]},
        //     {id: 13, accuracy: 0.556, hyperParameters: [{name: "gamma", value: "10"}, {name: "c", value:"10"}, {name: "kernel", value:"linear"}]},
        //     {id: 14, accuracy: 0.895, hyperParameters: [{name: "gamma", value: "50"}, {name: "c", value:"100"}, {name: "kernel", value:"linear"}]},
        //     {id: 15, accuracy: 0.999, hyperParameters: [{name: "gamma", value: "10"}, {name: "c", value:"1000"}, {name: "kernel", value:"linear"}]},
        //     {id: 16, accuracy: 0.588, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"0.1"}, {name: "kernel", value:"linear"}]},
        //     {id: 17, accuracy: 0.80015, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"1"}, {name: "kernel", value:"rbf"}]},
        //     {id: 18, accuracy: 0.56, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"10"}, {name: "kernel", value:"rbf"}]},
        //     {id: 19, accuracy: 0.698, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"100"}, {name: "kernel", value:"rbf"}]},
        //     {id: 20, accuracy: 0.53, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"1000"}, {name: "kernel", value:"rbf"}]},
        //     {id: 21, accuracy: 0.4568, hyperParameters: [{name: "gamma", value: "1000"}, {name: "c", value:"0.1"}, {name: "kernel", value:"linear"}]},
        //     {id: 22, accuracy: 0.967, hyperParameters: [{name: "gamma", value: "50"}, {name: "c", value:"1"}, {name: "kernel", value:"poly"}]},
        //     {id: 23, accuracy: 1, hyperParameters: [{name: "gamma", value: "1000"}, {name: "c", value:"10"}, {name: "kernel", value:"poly"}]},
        //     {id: 24, accuracy: 0.55, hyperParameters: [{name: "gamma", value: "1000"}, {name: "c", value:"100"},{name: "kernel", value:"linear"}]},
        //     {id: 25, accuracy: 0.69, hyperParameters: [{name: "gamma", value: "1000"}, {name: "c", value:"1000"},{name: "kernel", value:"linear"}]},
        //     {id: 25, accuracy: 0.867, hyperParameters: [{name: "gamma", value: "1000"}, {name: "c", value:"5"},{name: "kernel", value:"linear"}]},
        //     {id: 25, accuracy: 0.75, hyperParameters: [{name: "gamma", value: "50"}, {name: "c", value:"10"},{name: "kernel", value:"linear"}]},
        //     {id: 25, accuracy: 0.85, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"5"},{name: "kernel", value:"linear"}]},
        //     {id: 25, accuracy: 0.8989, hyperParameters: [{name: "gamma", value: "100"}, {name: "c", value:"10"},{name: "kernel", value:"linear"}]},
        //     {id: 26, accuracy: 0.645, hyperParameters: [{name: "gamma", value: "10"}, {name: "c", value:"5"},{name: "kernel", value:"linear"}]},
        //     {id: 27, accuracy: 0.755, hyperParameters: [{name: "gamma", value: "5"}, {name: "c", value:"50"},{name: "kernel", value:"linear"}]},
        //     {id: 27, accuracy: 0.645, hyperParameters: [{name: "gamma", value: "5"}, {name: "c", value:"5"},{name: "kernel", value:"linear"}]},
        //     {id: 27, accuracy: 0.7745, hyperParameters: [{name: "gamma", value: "5"}, {name: "c", value:"10"},{name: "kernel", value:"linear"}]},
        // ];
    }

    function sortFloat(a,b) { return a - b; }

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
        let hasNumber = /\d/;
        if (hasNumber.test(hyperParameterValues[0])) {
            let hyperParameterValuesNumber: number[] = [];
            hyperParameterValues.forEach(parameter => hyperParameterValuesNumber.push(Number((parameter))));
            console.log(hyperParameterValues);
            hyperParameterValuesNumber.sort(sortFloat);
            console.log(hyperParameterValuesNumber);
            hyperParameterValues = []
            hyperParameterValuesNumber.forEach(number => hyperParameterValues.push(fromExponential(number)))
            console.log(hyperParameterValues);
            return hyperParameterValues;
        }
        return hyperParameterValues.sort();
    }

    const addToResults = (resultToAdd:IndividualResult, tickValuesX: Array<string>, tickValuesY: Array<string>, results) => {
        results[tickValuesX.indexOf(resultToAdd.xAxisValue)][tickValuesY.indexOf(resultToAdd.yAxisValue)] = resultToAdd.result;
        return results;
    }

    const fillHeatMapResults = (results: number[][], runners: Runner[], xHyperParameter: string, yHyperParameter: string, tickValuesX: Array<string>, tickValuesY: Array<string>) =>
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

    const handleFirstHyperParameterSelection = (event: any) =>{
        setFirstHyperParameter(event.target.value);
    }

    const handleSecondHyperParameterSelection = (event: any) =>{
        setSecondHyperParameter(event.target.value);
    }

    const constructGraphData = (runners: Runner[]) => {
        let data: Data[] = [];
        runners.forEach(runner => {
            data.push({x: runner.id, y: runner.accuracy * 100});
        })

        return data;
    }

    if(isInErrorState)
    {
        return (
            <div>
                <HelperBox warning={true} content={"Dear user, please run the project at least two times to see the analysis of the aggregated results."} onClose={() => null}/>
                <p style={{textAlign: "center", marginTop: "100px"}}>No content to see here.</p>
            </div>)
    }

    if(isLoaded)
    {
        const tickValuesX = getHyperParameterValues(runners, firstHyperParameter);
        const tickValuesY = getHyperParameterValues(runners,  secondHyperParameter);

        let heatMapResults = Array(tickValuesX.length).fill(0).map(() => Array(tickValuesY.length));
        heatMapResults = fillHeatMapResults(heatMapResults, runners, firstHyperParameter, secondHyperParameter, tickValuesX, tickValuesY);
        const heatMapBins = HeatmapDataProvider.constructHeatMapBins(heatMapResults);

        const graphData = constructGraphData(runners);

        return (
            <div>
                <Navbar start="start-at-projects"/>

                <div className="summary-list">
                    <div className="summary-list-item">
                        <div className="total-list">
                            <div className="total">Total Runs: </div>
                                <div className="total-number slow">{runners.length}</div>
                                <div className="total ">Average Validation Accuracy [%]:</div>
                            <div className="total-number slow "> {(averageValidationResult * 100).toFixed(2)}</div>
                        </div>
                    </div>
                </div>

                <FadeIn>
                    <div className="heatmap-wrapper">
                        <div className="heatmap-title">
                            <h3>Parameters Heat Map</h3>
                        </div>
                        <div className="heatmap-structure-wrapper">
                            <div className="hyper-parameter-selector">
                                <p>X axis: </p>
                                <select value={firstHyperParameter} onChange={handleFirstHyperParameterSelection} className={"hyper-parameter-select"}>
                                    {hyperParameterSelector.map((parameter, key) => {
                                        return <option key={key} value={parameter} disabled={parameter === secondHyperParameter}>{parameter}</option>;
                                    })}
                                </select>
                            </div>
                            <div className="hyper-parameter-selector">
                                <p>Y axis: </p>
                                <select value={secondHyperParameter} onChange={handleSecondHyperParameterSelection} className={"hyper-parameter-select"}>
                                    {hyperParameterSelector.map((parameter, key) => {
                                        return <option key={key} value={parameter} disabled={parameter === firstHyperParameter}>{parameter}</option>;
                                    })}
                                </select>
                            </div>
                            <div className="analysis-heatmap">
                                <Heatmap bins={heatMapBins} tickValuesX={tickValuesX} tickValuesY={tickValuesY}
                                         width={600} height={520} xAxisLabel={firstHyperParameter + " [-]"}
                                         yAxisLabel={secondHyperParameter + " [-]"}/>
                                <div className="analysis-heatmap-legend">
                                    <LegendChart/>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="graph-wrapper">
                        <div className="analysis-graph">
                            <h3 className="underlined-text">Accuracy</h3>
                            <Chart data={graphData}/>
                        </div>
                    </div>
                </FadeIn>
            </div>
        )
    }
    return <p>LOADING</p>;
}

export default Analysis;
