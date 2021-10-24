import { EWOULDBLOCK } from "constants";
import React, { useState } from "react";
import Navbar from "./Navbar";
import ProjectService from "./ProjectService";

function Project()
{
    const [projectName, setProjectName] = useState("");
    const [files, setFiles] = useState([]);
    const [selectedModel, setSelectedModel] = useState("Support Vector Machines");

    const handleNameChange = (e: any) =>
    {
        e.preventDefault();
        setProjectName(e.target.value);
    }

    const handleFileUpload = (e: any) =>
    {
        e.preventDefault();
        console.log()
        setFiles(e.target.files);
    }

    const handleModelSelection = (e: any) =>
    {
        e.preventDefault();
        setSelectedModel(e.target.value);
    }

    const handleProjectSaving = (e: any) =>
    {
        e.preventDefault();
        ProjectService.save(projectName, files, selectedModel);
    }

    return (
        <div>
            <Navbar />
            <div className="project-form">
                <div className="project-form-block"> <input type="text" onChange={handleNameChange} placeholder="Name" /> </div>
                <div className="project-form-block"> <input type="file" className="file-input" onChange={handleFileUpload} multiple /></div>
                <div className="project-form-block">
                    Choose algorithm:
                    <select name="model-select" onChange={handleModelSelection}>
                        <option value="Support Vector Machines">Support Vector Machines</option>
                        <option value="Support Vector Machines">More models will be added in the future...</option>
                    </select>
                </div>
                <div className="project-form-block"><button className="save-button" onClick={handleProjectSaving}>Save</button></div>
            </div>
        </div>
    )
}

export default Project;