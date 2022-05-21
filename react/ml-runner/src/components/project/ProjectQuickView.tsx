import React from "react";
import {useHistory} from "react-router-dom";
import Popup from "reactjs-popup";
import RunnerList from "./RunnerList";

import SupportVectorMachinesRunnerForm from "./SupportVectorMachinesRunnerForm";
import RandomForestRunnerForm from "./RandomForestRunnerForm";

const POPUP_DIMENSIONS = {"width": "1100px", "min-height": "750px"};

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
                    <h2>{props.name} - {props.selectedModel}</h2>
                </div>
                <div className="project-control-panel-controls">
                    <Popup trigger={<button className="project-control-panel-button">Run</button>} position="right center" modal {...{contentStyle: POPUP_DIMENSIONS}}>
                        {close => renderRunnerForm(close)}
                    </Popup>
                    <button className="project-control-panel-button" onClick={redirectToAnalysisPage}>Analysis</button>
                </div>
            </div>
            <div className="runner-list">
                <RunnerList projectId={props.id} selectedModel={props.selectedModel}/>
            </div>
        </div>
    )
}

export default ProjectQuickView;