import React from "react";
import { Link } from "react-router-dom";
import playButton from '../styles/play-button.png';
import stopButton from '../styles/stop-button.png';

const Model = (props: any) =>
{
    return <div>
        <div className="control-panel">
            <Link to="/" onClick={(e) => props.handlePlayButtonClick(e, props.id)}><img className="play-button" src={playButton} alt="play_button" /></Link>
            <Link to="/" onClick={(e) => props.handleStopButtonClick(e, props.id)}><img className="stop-button" src={stopButton} alt="stop_button" /></Link>
        </div>
        {props.id}  &nbsp;&nbsp; {props.name}
    </div>;
}

export default Model;