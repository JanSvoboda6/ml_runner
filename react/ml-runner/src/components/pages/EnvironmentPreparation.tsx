import { AxiosResponse } from "axios";
import React, { useEffect, useState } from "react"
import { Redirect, useHistory } from "react-router";
import { toast, ToastContainer } from "react-toastify";
import DockerService from "../../services/DockerService";
import loading_motion from '../../styles/loading_motion.gif'

function EnvironmentPreparation()
{
    const [isPrepared, setPrepared] = useState(false);
    const history = useHistory();
    
    useEffect(() =>
    {
        DockerService.prepareContainer()
            .then(() =>{
                    setPrepared(true);
                }
            )
    }, [])

    if(isPrepared)
    {
        return <Redirect to="/" />
    }

    return(
    <div className="preparing-box">
            <p className="preparing-text preparing-slow">We are preparing your environment...</p>
            <img className='loading-motion' src={loading_motion} alt="loading_motion" />

    </div>
    )
}

export default EnvironmentPreparation;