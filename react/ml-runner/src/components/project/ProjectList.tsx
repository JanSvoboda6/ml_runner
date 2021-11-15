import React, { Component, useEffect, useState } from 'react';
import axios from "axios";
import loadingIcon from '../../styles/loading_icon.svg'
import ProjectQuickView from './ProjectQuickView';
import RunnerService from '../../services/RunnerService'
import { Link } from 'react-router-dom';

const API_URL = "http://localhost:8080/api";

interface Project
{
  id: number,
  name: string
}

const resultMap = new Map();

function ProjectList()
{
  const [isLoaded, setLoaded] = useState(false);
  const [projects, setProjects] = useState<Project[]>([]);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() =>
  {
    axios.get(API_URL + "/project")
      .then(
        (res) =>
        {
          setLoaded(true);
          setProjects(res.data);
        },
        (error) =>
        {
          setLoaded(true);
          setErrorMessage(error.message);
        }
      )
  }, [])

  if (!isLoaded)
  {
    return <div className="project-loading-message"><img className='loading-icon' src={loadingIcon} alt="loading_icon" /></div>;
  } else if (errorMessage || projects.length === 0)
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
        {projects.map(project => (
          <li key={project.id} className="project-item">
            <ProjectQuickView id={project.id} name={project.name} />
          </li>
        ))}
      </ul>
    );
  }
}

export default ProjectList;