package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.project.Project;
import com.jan.web.project.ProjectRepository;
import com.jan.web.security.ValidationException;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestValidatorImpl implements RequestValidator
{
    private final ProjectRepository projectRepository;
    private final ContainerRepository containerRepository;
    private final RunnerRepository runnerRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestValidatorImpl(ProjectRepository projectRepository, ContainerRepository containerRepository, RunnerRepository runnerRepository, UserRepository userRepository)
    {
        this.projectRepository = projectRepository;
        this.containerRepository = containerRepository;
        this.runnerRepository = runnerRepository;
        this.userRepository = userRepository;
    }

    //TODO Jan: Validate whether project is owned by a user
    @Override
    public Project validateProject(long projectId)
    {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent())
        {
            return project.get();
        }
        throw new ValidationException("The project with id " + projectId + " cannot be found!");
    }

    @Override
    public ContainerEntity validateContainerEntity(long containerEntityId)
    {
        Optional<ContainerEntity> containerEntity = containerRepository.findById(containerEntityId);
        if (containerEntity.isPresent())
        {
            return containerEntity.get();
        }
        throw new ValidationException("The container with id " + containerEntityId + " cannot be found!");
    }

    @Override
    public Runner validateRunner(long runnerId)
    {
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isPresent())
        {
            return runner.get();
        }
        throw new ValidationException("The runner with id " + runnerId + " cannot be found!");
    }

    @Override
    public User validateUser(String username)
    {
       Optional<User> user = userRepository.findByUsername(username);
       if(user.isPresent())
       {
           return user.get();
       }
        throw new ValidationException("The user with username " + username + " cannot be found!");
    }
}
