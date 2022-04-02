import React from "react";
import {useHistory } from "react-router-dom";
import Popup from "reactjs-popup";
import RunnerForm from "./RunnerForm";
import RunnerList from "./RunnerList";

const ProjectQuickView = (props: any) =>
{
    let history = useHistory();
    const redirectToAnalysisPage = () =>
    {
        history.push('/analysis');
    }

    return (<div className="project-quick-view">
        <div className="control-panel">
            <Popup trigger={<button className="project-control-panel-button">Run</button>} position="right center" modal>
                {close => (<RunnerForm projectName={props.name} projectId={props.id} handleRunButton={() => close()} />)}
            </Popup>
            <button className="project-control-panel-button" onClick={redirectToAnalysisPage}>Analysis</button>
        </div>
        <div className="project-name">
            <h2>{props.name}</h2>
        </div>
        <div className="runner-list">
            <RunnerList projectId={props.id} />
        </div>
    </div>
    )
}

export default ProjectQuickView;