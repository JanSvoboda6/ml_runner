import axios, { AxiosResponse } from "axios";
import React, { useEffect } from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import Popup from "reactjs-popup";
import RunnerService from "../../services/RunnerService";
import playButton from '../../styles/play-button.png';
import stopButton from '../../styles/stop-button.png';
import ProjectStatistics from "../visualization/ProjectStatistics";
import RunnerForm from "./RunnerForm";
import RunnerList from "./RunnerList";

const API_URL = "http://localhost:8080/api/project";

const ProjectQuickView = (props: any) =>
{
    return (<div>
        <div className="control-panel">
            <Popup trigger={<button className="project-control-panel-button">Run</button>} position="right center" modal>
                {close => (<RunnerForm projectName={props.name} projectId={props.id} handleRunButton={() => close()} />)}
            </Popup>
            <Popup trigger={<button className="project-control-panel-button">Statistics</button>} position="right center" modal>
                {close => (<ProjectStatistics projectName={props.name} projectId={props.id} handleCloseButton={() => close()} />)}
            </Popup>
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