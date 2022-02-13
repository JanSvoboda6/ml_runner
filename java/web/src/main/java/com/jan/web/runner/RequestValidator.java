package com.jan.web.runner;

import com.jan.web.Project;
import com.jan.web.docker.ContainerEntity;

public interface RequestValidator
{
    Project validateProject(long projectId);
    ContainerEntity validateContainerEntity(long containerEntityId);
    Runner validateRunner(long runnerId);
}
