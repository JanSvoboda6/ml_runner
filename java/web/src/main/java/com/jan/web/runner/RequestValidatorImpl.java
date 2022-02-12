package com.jan.web.runner;

import com.jan.web.Project;
import com.jan.web.ProjectRepository;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RequestValidatorImpl implements RequestValidator
{
    private final ProjectRepository projectRepository;
    private final ContainerRepository containerRepository;

    @Autowired
    public RequestValidatorImpl(ProjectRepository projectRepository, ContainerRepository containerRepository)
    {
        this.projectRepository = projectRepository;
        this.containerRepository = containerRepository;
    }

    @Override
    public Project validateProject(long projectId)
    {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent())
        {
            return project.get();
        }
        throw new RuntimeException("The project with id " + projectId + " cannot be found!");
    }

    @Override
    public ContainerEntity validateContainerEntity(long containerEntityId)
    {
        return null;
    }
}
