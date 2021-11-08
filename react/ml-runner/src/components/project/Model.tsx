import React from "react";
import { Link } from "react-router-dom";
import playButton from '../../styles/play-button.png';
import stopButton from '../../styles/stop-button.png';


const Model = (props: any) =>
{
    if (props.isRunning)
    {
        return <div> Running...</div>
    }

    if (props.result !== undefined)
    {
        return <div>
            <div>Validation result of first label: {(props.result.validationResultFirstLabel * 100).toFixed(1)}%</div>
            <div>Validation result of second label: {(props.result.validationResultSecondLabel * 100).toFixed(2)}%</div>
        </div>
    }

    return <div>
        <div className="control-panel">
            <Link to="/" onClick={(e) => props.handlePlayButtonClick(e, props.id)}><img className="play-button" src={playButton} alt="play_button" /></Link>
            <Link to="/" onClick={(e) => props.handleStopButtonClick(e, props.id)}><img className="stop-button" src={stopButton} alt="stop_button" /></Link>
        </div>
        {props.name}
    </div>;
}

export default Model;