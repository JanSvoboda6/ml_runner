import React from "react";
import {useHistory} from "react-router-dom";
import Popup from "reactjs-popup";
import RunnerList from "./RunnerList";

import {BACKEND_URL} from "../../helpers/url";
import SupportVectorMachinesRunnerForm from "./SupportVectorMachinesRunnerForm";
import RandomForestRunnerForm from "./RandomForestRunnerForm";

const API_URL = BACKEND_URL + "/api";

const ProjectQuickView = (props: any) =>
{
    let history = useHistory();
    const redirectToAnalysisPage = () =>
    {
        history.push('/analysis?projectId=' + props.id);
    }

    const renderRunnerForm = (popupClosingAction: Function) =>
    {
        switch (props.selectedModel)
        {
            case "Support Vector Machines": return <SupportVectorMachinesRunnerForm projectName={props.name} projectId={props.id} handleRunButton={() => popupClosingAction()}/>
            case "Random Forest": return <RandomForestRunnerForm projectName={props.name} projectId={props.id} handleRunButton={() => popupClosingAction()}/>
        }
    }

    return (
        <div className="project-quick-view">
            <div className="control-panel">
                <div className="project-name">
                    <h2>{props.name}</h2>
                </div>
                <div className="project-control-panel-controls">
                    <Popup trigger={<button className="project-control-panel-button">Run</button>} position="right center" modal>
                        {close => renderRunnerForm(close)}
                    </Popup>
                    <button className="project-control-panel-button" onClick={redirectToAnalysisPage}>Analysis</button>
                </div>
            </div>
            <div className="runner-list">
                <RunnerList projectId={props.id} />
            </div>
        </div>
    )
}

export default ProjectQuickView;