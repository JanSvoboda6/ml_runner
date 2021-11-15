
import React, { useState } from "react";
import Navbar from "../navigation/Navbar";
import ProjectService from "../project/ProjectService";
import { Redirect } from "react-router";
import 'reactjs-popup/dist/index.css';
import Popup from "reactjs-popup";
import Datasets from "../dataset/Datasets";
import "../../styles/Project.css";
import SelectableDataset from "../dataset/SelectableDataset";

function Project()
{
    const [projectName, setProjectName] = useState("");
    const [firstLabel, setFirstLabelName] = useState("");
    const [secondLabel, setSecondLabelName] = useState("");
    const [firstLabelFolder, setFirstLabelFolder] = useState("");
    const [secondLabelFolder, setSecondLabelFolder] = useState("");
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

    const handleFirstLabelFolderSelection = (folder) =>
    {
        console.log(folder);
        setFirstLabelFolder(folder);
    }

    const handleSecondLabelFolderSelection = (folder) =>
    {
        console.log(folder);
        setSecondLabelFolder(folder);
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
        ProjectService.save(projectName, firstLabel, secondLabel, firstLabelFolder, secondLabelFolder, selectedModel)
            .then(() =>
            {
                setSuccessfullySaved(true);
            }
            );
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
                        {close => (
                            <SelectableDataset handleFolderSelection={(folder) => { handleFirstLabelFolderSelection(folder); close(); }} />
                        )
                        }
                    </Popup>
                    {firstLabelFolder && <div>Selected: {firstLabelFolder}</div>}
                </div>
                <div className="project-form-block project-form-block-data">
                    <input className="label-name" type="text" onChange={handleSecondLabelChange} placeholder="Second label" />
                    <Popup trigger={<button className="data-folder-button"> Choose Folder</button>} position="right center" modal>
                        {close => (
                            <SelectableDataset handleFolderSelection={(folder) => { handleSecondLabelFolderSelection(folder); close(); }} />
                        )
                        }
                    </Popup>
                    {secondLabelFolder && <div>Selected: {secondLabelFolder}</div>}
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