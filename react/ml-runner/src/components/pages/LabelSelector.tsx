import Popup from "reactjs-popup";
import SelectableDataset from "../dataset/SelectableDataset";
import React, {useState} from "react";

function LabelSelector(props: any)
{
    const [labelName, setLabelName] = useState("");
    const [folderPath, setFolderPath] = useState("");

    const handleLabelNameChange = (e: any) => {
        setLabelName(e.target.value);
        props.handleChange(props.id, e.target.value, folderPath)
    }

    const handleFolderChange = (folderPath: string) => {
        setFolderPath(folderPath);
        props.handleChange(props.id, labelName, folderPath)
    }

    return (
        <>
            <input className="project-label-field input-text" type="text" onChange={handleLabelNameChange} placeholder="Label Name"/>
            <Popup trigger={<button className="data-folder-button"> Choose Folder</button>} position="right center"
                   modal>
                {close => (
                    <SelectableDataset handleFolderSelection={(folderPath) => {
                        handleFolderChange(folderPath);
                        close();
                    }}/>
                )}
            </Popup>
            {folderPath && <div className="text-confirm">Selected: {folderPath}</div>}
        </>
    )
}

export default LabelSelector;