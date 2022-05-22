import Popup from "reactjs-popup";
import React from "react";

const contentStyle = {"width": "250px", "minHeight": "30px", background: "rgba(255,255,255,0.86)", borderWidth: "0px", color: "black", fontSize:"13px"};
const arrowStyle = { color: "#ffffff" };

interface ParameterInfoTooltipProps
{
    textContent: string
}

/**
 * Component that could be viewed by hovering on the '?' question mark when submitting a runner.
 */
function ParameterInfoTooltip({textContent}:ParameterInfoTooltipProps) {
return (
    <Popup
        trigger={<button tabIndex={-1} className="parameter-info-button"> ? </button>}
        position="right center"
        closeOnDocumentClick
        on={["hover", "focus"]}
        arrow={true}
        {...{contentStyle, arrowStyle}}
    >
        <p className={"parameter-info-text"}> {textContent} </p>
    </Popup>)
}

export default ParameterInfoTooltip;