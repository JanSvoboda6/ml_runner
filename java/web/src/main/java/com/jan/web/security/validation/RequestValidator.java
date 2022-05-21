package com.jan.web.security.validation;

import com.jan.web.project.Project;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.runner.Runner;
import com.jan.web.security.user.User;

public interface RequestValidator
{
    Project validateProject(long projectId, User user);
    Runner validateRunner(long runnerId, User user);
    ContainerEntity validateContainerEntity(long containerEntityId);
    User validateUser(String username);
    User validateUserFromJwtToken(String token);
}
