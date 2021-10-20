import React, { Component, useEffect, useState } from 'react';
import axios from "axios";
import loadingIcon from '../styles/loading_icon.svg'
import Model from './Model';
import RunnerService from '../services/RunnerService'

const API_URL = "http://localhost:8080/api";

interface Model
{
  id: number,
  name: string
}

function ModelList()
{
  const [isLoaded, setLoaded] = useState(false);
  const [models, setModels] = useState<Model[]>([]);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() =>
  {
    axios.get(API_URL + "/models")
      .then(
        (res) =>
        {
          setLoaded(true);
          setModels(res.data);
        },
        (error) =>
        {
          setLoaded(true);
          setErrorMessage(error.message);
        }
      )
  }, [])


  const handlePlayButtonClick = (e: any, id: number) =>
  {
    RunnerService.run();
  }

  const handleStopButtonClick = (e: any, id: number) =>
  {
    RunnerService.stop();
  }

  if (errorMessage)
  {
    return <div>Error: {errorMessage}</div>;
  } else if (!isLoaded)
  {
    return <div className="project-loading-message"><img className='loading-icon' src={loadingIcon} alt="loading_icon" /></div>;
  } else
  {
    return (
      <ul className="project-list">
        {models.map(model => (
          <li key={model.id} className="project-item">
            <Model id={model.id} name={model.name} handlePlayButtonClick={handlePlayButtonClick} handleStopButtonClick={handleStopButtonClick} />
          </li>
        ))}
      </ul>
    );
  }
}

export default ModelList;