import axios, {AxiosResponse} from "axios";
import React from "react";
import {useEffect, useState} from "react";
import FadeIn from "react-fade-in";
import authorizationHeader from "../../services/AuthorizationHeader";
import Runner from "./Runner";
import {BACKEND_URL} from "../../helpers/url";

const API_URL = BACKEND_URL + "/api/project";

interface Runner
{
    projectId: number,
    runnerId: number
}

/**
 * Represent a list of runners.
 */
function RunnerList(props)
{
    const [isLoaded, setLoaded] = useState(false);
    const [runners, setRunners] = useState<Runner[]>([]);

    useEffect(() =>
    {
        axios.get(API_URL + "/runners?projectId=" + props.projectId, {headers: authorizationHeader()})
            .then(
                (res: AxiosResponse<any>) =>
                {
                    setLoaded(true);

                    const runners: Array<Runner> = [];
                    res.data.forEach(runner =>
                    {
                        runners.push({projectId: runner.project.id, runnerId: runner.id})
                    });
                    setRunners(runners.reverse());
                },
                () =>
                {
                    setLoaded(true);
                }
            )
    }, [])

    if (!isLoaded)
    {
        return <div> Loading...</div>
    }

    return (
        <div>
            <ul className="runner-list-inside">
                <FadeIn delay={25}>
                    <div className="runner-list-table">
                        <p className="runner-list-table-header-text">Runner Id</p>
                        <p className="runner-list-table-header-text">Parameters</p>
                        <p className="runner-list-table-header-text">Date</p>
                        <p className="runner-list-table-header-text">Status</p>
                        <p className="runner-list-table-header-text">Accuracy</p>
                    </div>
                    {runners.length == 0 ?
                        <div>
                            <hr className="grey-horizontal"/>
                            <p className="runner-list-initial-message">No runs have been yet executed.</p>
                        </div> :
                        <div>{
                            runners.map(runner => (
                                <div key={runner.runnerId}>
                                    <hr className="grey-horizontal"/>
                                    <li className="runner-item">
                                        <Runner projectName={props.projectName} projectId={runner.projectId}
                                                runnerId={runner.runnerId} selectedModel={props.selectedModel}/>
                                    </li>
                                </div>
                            ))
                        } </div>
                    }
                </FadeIn>
            </ul>
        </div>
    )
}

export default RunnerList;