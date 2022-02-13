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
    private final RunnerRepository runnerRepository;

    @Autowired
    public RequestValidatorImpl(ProjectRepository projectRepository, ContainerRepository containerRepository, RunnerRepository runnerRepository)
    {
        this.projectRepository = projectRepository;
        this.containerRepository = containerRepository;
        this.runnerRepository = runnerRepository;
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
        Optional<ContainerEntity> containerEntity = containerRepository.findById(containerEntityId);
        if (containerEntity.isPresent())
        {
            return containerEntity.get();
        }
        throw new RuntimeException("The container with id " + containerEntityId + " cannot be found!");
    }

    @Override
    public Runner validateRunner(long runnerId)
    {
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isPresent())
        {
            return runner.get();
        }
        throw new RuntimeException("The runner with id " + runnerId + " cannot be found!");
    }
}
