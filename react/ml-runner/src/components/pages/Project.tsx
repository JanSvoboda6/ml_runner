import React, {useState} from "react";
import Navbar from "../navigation/Navbar";
import ProjectService from "../project/ProjectService";
import {Redirect} from "react-router";
import 'reactjs-popup/dist/index.css';
import "../../styles/Project.css";
import FadeIn from "react-fade-in";
import LabelSelector from "../project/LabelSelector";
import {Label} from "../../helpers/types";

/**
 * Component used for creating a project.
 */
function Project()
{
    const [projectName, setProjectName] = useState("");
    const [selectedModel, setSelectedModel] = useState("Support Vector Machines");
    const [isSuccessfullySaved, setSuccessfullySaved] = useState(false);
    const [labels, setLabels] = useState<Label[]>([{id: 0, name: "", folderPath: ""}, {id: 1, name: "", folderPath: ""}]);

    const handleNameChange = (e: { target: { value: string; }; }) =>
    {
        setProjectName(e.target.value);
    }

    const handleLabelChange = (id:number, labelName: string, folderPath:string) => {
        const label: Label = {
            id: id,
            name: labelName,
            folderPath: folderPath
        };

       const indexOfLabel = labels.findIndex(label => label.id === id);
       labels[indexOfLabel] = label;
       setLabels(labels);
    }

    const handleModelSelection = (e: any) =>
    {
        e.preventDefault();
        setSelectedModel(e.target.value);
    }

    const addLabel = () => {
        const label: Label = {
            id: labels.length,
            name: "",
            folderPath: ""
        }
        setLabels(previousLabels => [...previousLabels, label]);
    }

    const removeLastLabel = () => {
        let purgedLabels = labels.filter(label => label.id !== labels.length - 1);
        setLabels(purgedLabels);
    }

    const handleProjectSaving = (e: any) =>
    {
        e.preventDefault();
        ProjectService.save(projectName, selectedModel, labels)
            .then(() =>
            {
                setSuccessfullySaved(true);
            });
    }

    if (isSuccessfullySaved)
    {
        return <Redirect to='/' />;
    }

    return (
        <div>
            <Navbar/>
            <FadeIn>
                <div className="project-form">
                    <div className="project-form-block"> <h3>Create New Project</h3> </div>
                    <div className="project-form-block"> <input type="text" className="input-text" onChange={handleNameChange} placeholder="Project Name" /> </div>
                    <div>
                        {labels.map(label => {
                            return(
                                <div key={label.id} className="project-form-block project-form-block-data">
                                    <LabelSelector id={label.id} handleChange={(id, labelName, folderPath) => handleLabelChange(id, labelName, folderPath)}/>
                                </div>
                            )})}
                    </div>

                    <div className="project-form-block project-form-block-data">
                     <button className="standard-button" onClick={addLabel}>Add Label</button>
                       <button className="standard-button" onClick={removeLastLabel} disabled={labels.length === 2}>Remove Last Label</button>
                    </div>

                    <div className="project-form-block">
                        <div className="model-select-text">Choose algorithm: </div>
                        <select name="model-select" onChange={handleModelSelection}>
                            <option className="model-select-option" value="Support Vector Machines">Support Vector Machines</option>
                            <option className="model-select-option" value="Random Forest">Random Forest</option>
                        </select>
                    </div>
                    <div className="project-form-block"><button className="save-button" onClick={handleProjectSaving}>Save</button></div>
            </div>
            </FadeIn>
        </div>
    )
}

export default Project;