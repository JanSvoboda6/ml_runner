import React, { useEffect, useState } from 'react';
import axios from "axios";
import loadingAnimation from '../../styles/loading_graphics.gif';
import ProjectQuickView from './ProjectQuickView';
import authorizationHeader from "../../services/AuthorizationHeader";
import { Link } from 'react-router-dom';
import FadeIn from 'react-fade-in/lib/FadeIn';

import {BACKEND_URL} from "../../helpers/url";
const API_URL = BACKEND_URL + "/api";

interface Project
{
  id: number,
  name: string,
  selectedModel: string
}

function ProjectList()
{
  const [isLoaded, setLoaded] = useState(false);
  const [projects, setProjects] = useState<Project[]>([]);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() =>
  {
    axios.get<any>(API_URL + "/project", { headers: authorizationHeader() })
      .then(
        (res) =>
        {
            setProjects(res.data.reverse());
            setLoaded(true);
        },
        (error) =>
        {
            setErrorMessage(error.message);
            setLoaded(true);
        }
      )
  }, [])

  if (!isLoaded)
  {
    return <FadeIn>
                <div className='loading-animation-wrapper'>
                    <img className='project-loading-animation' src={loadingAnimation} alt="loading animation" />
                </div>
          </FadeIn>;
  } else if (errorMessage || projects.length === 0)
  {
    return (
        <FadeIn>
          <ul className="project-list">
            <div className="project-text">No projects have been yet created.</div>
            <Link to="/newproject"><button className="project-create-new-button">Add New Project</button></Link>
          </ul>
        </FadeIn>)
  } else
  {
    return (
      <ul className="project-list">
        {projects.map(project => (
          <li key={project.id} className="project-item">
            <ProjectQuickView id={project.id} name={project.name} selectedModel={project.selectedModel}/>
          </li>
        ))}
      </ul>
    );
  }
}

export default ProjectList;