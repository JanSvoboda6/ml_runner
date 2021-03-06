import React, {useEffect, useState} from "react"
import FadeIn from 'react-fade-in';
import {useHistory} from "react-router";
import DockerService from "../../services/DockerService";
import cubeMerging from '../../styles/cube_merging.gif'
import HelperBox from "../navigation/HelperBox";

/**
 * Component used for contacting API that prepares container for the user.
 */
function EnvironmentPreparation()
{
    const [isPrepared, setPrepared] = useState(false);
    const history = useHistory();

    useEffect(() =>
    {
        DockerService.prepareContainer()
            .then(() =>
            {
                setPrepared(true);
            })
    }, [])

    if (isPrepared)
    {
        setTimeout(() => {history.push('/')}, 2000)
    }

    return (
        <div>
            <HelperBox content="You have been successfully logged in!" onClose={() => null}/>
            <FadeIn delay={200}>
                <div className="preparing-box">
                    <div className="preparing-text-wrapper">
                        <p className="preparing-text">We are preparing your environment...</p>
                    </div>
                    <img className="loading-motion" src={cubeMerging} alt="loading_motion" />
                </div>
            </FadeIn>
        </div>
    )
}

export default EnvironmentPreparation;