
import React, { useState } from "react";
import Navbar from "../navigation/Navbar";
import ModelService from "../project/ModelService";
import { Redirect } from "react-router";
import 'reactjs-popup/dist/index.css';
import Popup from "reactjs-popup";
import Datasets from "../dataset/Datasets";
import "../styles/Project.css";

function Project()
{
    const [projectName, setProjectName] = useState("");
    const [firstLabel, setFirstLabelName] = useState("");
    const [secondLabel, setSecondLabelName] = useState("");
    const [filesOfFirstLabel, setFilesOfFirstLabel] = useState([]);
    const [filesOfSecondLabel, setFilesOfSecondLabel] = useState([]);
    const [selectedModel, setSelectedModel] = useState("Support Vector Machines");
    const [isSuccessfullySaved, setSuccessfullySaved] = useState(false);

    const handleNameChange = (e: any) =>
    {
        e.preventDefault();
        setProjectName(e.target.value);
    }

    const handleFirstLabelChange = (e: any) =>
    {
        e.preventDefault();
        setFirstLabelName(e.target.value);
    }

    const handleSecondLabelChange = (e: any) =>
    {
        e.preventDefault();
        setSecondLabelName(e.target.value);
    }

    const handleFirstFileUpload = (e: any) =>
    {
        e.preventDefault();
        setFilesOfFirstLabel(e.target.files);
    }

    const handleSecondFileUpload = (e: any) =>
    {
        e.preventDefault();
        setFilesOfSecondLabel(e.target.files);
    }

    const handleModelSelection = (e: any) =>
    {
        e.preventDefault();
        setSelectedModel(e.target.value);
    }

    const handleProjectSaving = (e: any) =>
    {
        e.preventDefault();
        //TODO Jan: Decide whether to use project or model name
        ModelService.save(projectName, firstLabel, secondLabel, filesOfFirstLabel, filesOfSecondLabel, selectedModel)
            .then(() =>
            {
                setSuccessfullySaved(true);
            }
            );
    }

    const handleDatasetOpening = (e: any) =>
    {

    }

    if (isSuccessfullySaved)
    {
        return <Redirect to='/' />;
    }

    return (
        <div>
            <Navbar start="start-at-new-project" />
            <div className="project-form">
                <div className="project-form-block"> <input type="text" onChange={handleNameChange} placeholder="Project Name" /> </div>
                <div className="project-form-block project-form-block-data">
                    <input className="label-name" type="text" onChange={handleFirstLabelChange} placeholder="First label" />
                    <Popup trigger={<button className="data-folder-button"> Choose Folder</button>} position="right center" modal>
                        <div>
                            <button className="choose-data-folder-button">Choose Folder</button>
                            <Datasets />
                        </div>
                    </Popup>
                </div>
                <div className="project-form-block project-form-block-data">
                    <input className="label-name" type="text" onChange={handleSecondLabelChange} placeholder="Second label" />
                    <Popup trigger={<button className="data-folder-button"> Choose Folder</button>} position="right center" modal>
                        <div>
                            <button className="choose-data-folder-button">Choose Folder</button>
                            <Datasets />
                        </div>
                    </Popup>
                </div>
                <div className="project-form-block">
                    <div className="model-select-text">Choose algorithm: </div>
                    <select name="model-select" onChange={handleModelSelection}>
                        <option value="Support Vector Machines">Support Vector Machines</option>
                        <option value="Support Vector Machines">More models will be added in the future...</option>
                    </select>
                </div>
                {/* <div className="project-form-block"><button className="open-dataset-button" onClick={handleDatasetOpening}>Open</button></div> */}
                <div className="project-form-block"><button className="save-button" onClick={handleProjectSaving}>Save</button></div>
            </div>
        </div>
    )
}

export default Project;