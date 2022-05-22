import React, { useState } from "react";
import Datasets from "./Datasets";

/**
 * Used for selecting a folder (dataset) for specific class while creating a Project.
 */
function SelectableDataset(props: { handleFolderSelection: (folderPath: string) => void })
{
    const [folderPath, setFolderPath] = useState("");

    const handleFolderSelection = (folder) =>
    {
        setFolderPath(folder.key);
    }

    return (
        <div>
            <button className="choose-data-folder-button" onClick={() => props.handleFolderSelection(folderPath)}>Choose Folder</button>
            <Datasets handleFolderSelection={(folder) => handleFolderSelection(folder)} />
        </div>
    )
}

export default SelectableDataset;