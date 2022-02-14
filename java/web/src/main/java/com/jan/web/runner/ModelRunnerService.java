package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.docker.ContainerEntity;

public interface ModelRunnerService
{
   void runProject(RunRequest request, Project project, ContainerEntity containerEntity);
}
