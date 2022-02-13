package com.jan.web.runner;

import com.jan.web.Project;
import com.jan.web.RunRequest;
import com.jan.web.docker.ContainerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ModelRunnerServiceImpl implements ModelRunnerService
{
    RunnerRepository runnerRepository;
    ProjectRunner projectRunner;

    @Autowired
    public ModelRunnerServiceImpl(RunnerRepository runnerRepository, ProjectRunner projectRunner)
    {
        this.runnerRepository = runnerRepository;
        this.projectRunner = projectRunner;
    }

    @Override
    public void runProject(RunRequest request, Project project, ContainerEntity containerEntity)
    {
        Runner runner = new Runner();
        runner.setProject(project);
        runner.setGammaParameter(request.getGammaParameter());
        runner.setCParameter(request.getcParameter());
        runner.setFinished(false);
        runnerRepository.save(runner);

        projectRunner.run(runner, containerEntity.getId());
    }
}
