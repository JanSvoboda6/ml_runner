import React from "react";

/**
 * Popup component.
 */
const Popup = (props: { handleClose: React.MouseEventHandler<HTMLSpanElement>; content: string }) =>
{
    return (
        <div className="popup-box">
            <div className="box">
                <span className="close-icon" onClick={props.handleClose}>x</span>
                {props.content}
            </div>
        </div>
    );
};

export default Popup;