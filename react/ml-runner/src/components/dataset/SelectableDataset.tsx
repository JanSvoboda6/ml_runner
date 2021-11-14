
import React, { useState } from "react";
import Datasets from "./Datasets";

function SelectableDataset(props)
{
    const [folder, setFolder] = useState("");

    const handleFolderSelection = (folder) =>
    {
        setFolder(folder.key);
    }

    return (
        <div>
            <button className="choose-data-folder-button" onClick={() => props.handleFolderSelection(folder)}>Choose Folder</button>
            <Datasets handleFolderSelection={(folder) => handleFolderSelection(folder)} />
        </div>
    )
}

export default SelectableDataset;