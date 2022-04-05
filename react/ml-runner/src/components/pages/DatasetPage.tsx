import React, {useState} from "react";
import Datasets from "../dataset/Datasets";
import Navbar from "../navigation/Navbar";
import HelperBox from "../navigation/HelperBox";

function DatasetPage()
{
    const [warning, setWarning] = useState("");
    const onWarningClose = () => {
        setWarning("");
    }
    return (
        <>
             <div className="wrapper">
                {warning && <HelperBox content={warning} warning={true} onClose={onWarningClose}/>}
            <Navbar start="start-at-datasets"/>
             </div>

            <Datasets onWarning={warningMessage => setWarning(warningMessage)}/>
        </>
    )
}

export default DatasetPage;