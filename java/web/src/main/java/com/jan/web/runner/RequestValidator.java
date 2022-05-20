package com.jan.web.runner;

import com.jan.web.project.Project;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.security.user.User;

public interface RequestValidator
{
    Project validateProject(long projectId);
    ContainerEntity validateContainerEntity(long containerEntityId);
    Runner validateRunner(long runnerId);
    User validateUser(String username);
}
