import React, { useState } from "react";

function HelperBox(props: any)
{
    const [isHidden, setHidden] = useState(false);

    const handleClose = () =>
    {
        setHidden(true);
        props.onClose();
    }

    if (isHidden)
    {
        return null;
    }

    return (
        <div className="helper-box" >
            <div className="left-item" />
            <div className={props.warning? "helper-box-text-warning" : "helper-box-text-confirm"}>{props.content}</div>
            <div className="helper-box-button-container">
                <button className="helper-box-button" onClick={handleClose}>X</button>
            </div> 
        </div >
    )
}

export default HelperBox;