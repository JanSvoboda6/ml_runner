import { AxiosResponse } from "axios";
import React, { useEffect, useState } from "react"
import FadeIn from 'react-fade-in';
import { Redirect, useHistory } from "react-router";
import { toast, ToastContainer } from "react-toastify";
import DockerService from "../../services/DockerService";
import loading_motion from '../../styles/loading_motion.gif'
import cubeBuilding from '../../styles/cube_building.gif'
import HelperBox from "../navigation/HelperBox";

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
            }
            )
    }, [])

    if (isPrepared)
    {
        setTimeout(() => history.push('/'), 2000)
    }

    return (
        <div>
            <HelperBox content="You have been successfully logged in!" />
            <FadeIn delay={200}>
                <div className="preparing-box">
                    <div className="preparing-text-wrapper">
                        <p className="preparing-text">We are preparing your environment</p>
                        {/* <span className="loading-dots" /> */}
                    </div>
                    <img className="loading-motion" src={cubeBuilding} alt="loading_motion" />
                </div>
            </FadeIn>
        </div>
    )
}

export default EnvironmentPreparation;