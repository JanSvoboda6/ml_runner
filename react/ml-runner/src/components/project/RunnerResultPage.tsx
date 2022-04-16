import {useLocation} from "react-router";
import queryString from 'query-string';
import React from "react";
import RunnerResult from "./RunnerResult";
import Navbar from "../navigation/Navbar";

function RunnerResultPage()
{
    const location = useLocation();
    const parameters = queryString.parse(location.search);
    const projectId = parseInt(parameters.project);
    const runnerId = parseInt(parameters.runner);
    return(
        <>
            <Navbar/>
            <RunnerResult projectId={projectId} runnerId={runnerId}/>
        </>
    )
}

export default RunnerResultPage;