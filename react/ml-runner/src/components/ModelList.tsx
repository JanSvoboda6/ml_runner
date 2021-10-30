import React, { Component, useEffect, useState } from 'react';
import axios from "axios";
import loadingIcon from '../styles/loading_icon.svg'
import Model from './Model';
import RunnerService from '../services/RunnerService'
import { Link } from 'react-router-dom';

const API_URL = "http://localhost:8080/api";

interface Model
{
  id: number,
  name: string
}

const resultMap = new Map();

function ModelList()
{
  const [isLoaded, setLoaded] = useState(false);
  const [models, setModels] = useState<Model[]>([]);
  const [errorMessage, setErrorMessage] = useState("");
  const nonExistingId = -1;
  const [idOfRunningModel, setIdOfRunningModel] = useState(nonExistingId);

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


  const handleRunButtonClick = (e: any, id: number) =>
  {
    setIdOfRunningModel(id);
    RunnerService.run(id)
      .then((res) =>
      {
        const validationResult = {
          'validationResultFirstLabel': res.data.validationResultFirstLabel,
          'validationResultSecondLabel': res.data.validationResultSecondLabel
        }
        resultMap.set(id, validationResult);
      })
      .catch(error =>
      {
        var message = "";
        if (error && error.response && error.response.data.message)
        {
          message = error.response.data.message;
        }
        else if (error.message)
        {
          message = error.message;
        }
        else if (error.toString())
        {
          message = error.toString();
        }
        console.log(message);
      })
      .finally(() =>
      {
        setIdOfRunningModel(nonExistingId);
      }
      );
  }

  const handleStopButtonClick = (e: any, id: number) =>
  {
    RunnerService.stop(id);
  }

  const handleValidationResultHiding = (e: any, id: number) =>
  {
    //TODO Jan: Implement proper map state handling
    resultMap.delete(id);
  }
  if (!isLoaded)
  {
    return <div className="project-loading-message"><img className='loading-icon' src={loadingIcon} alt="loading_icon" /></div>;
  } else if (errorMessage || models.length === 0)
  {
    return (
      <ul className="project-list">
        <div className="project-text">No projects have been yet created.</div>
        <Link to="/newproject"><button className="project-create-new-button">Add New Project</button></Link>
      </ul>)
  } else
  {
    return (
      <ul className="project-list">
        {models.map(model => (
          <li key={model.id} className="project-item">
            <Model id={model.id}
              name={model.name}
              handlePlayButtonClick={handleRunButtonClick}
              handleStopButtonClick={handleStopButtonClick}
              isRunning={idOfRunningModel === model.id}
              hideValidationResult={handleValidationResultHiding}
              result={resultMap.get(model.id)} />
          </li>
        ))}
      </ul>
    );
  }
}

export default ModelList;