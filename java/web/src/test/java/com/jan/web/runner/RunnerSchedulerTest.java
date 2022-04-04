package com.jan.web.runner;

import com.jan.web.docker.ContainerRepository;
import com.jan.web.security.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RunnerSchedulerTest
{

    private RunnerScheduler runnerScheduler;
    private RunnerService runnerService;
    private ContainerRepository containerRepository;
    private UserRepository userRepository;
    private ProjectRunner projectRunner;
    private RunnerQueueRepository runnerQueueRepository;

    @BeforeEach
    public void before()
    {
        runnerService = Mockito.mock(RunnerService.class);
        containerRepository = Mockito.mock(ContainerRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        projectRunner = Mockito.mock(ProjectRunner.class);
        runnerQueueRepository = Mockito.mock(RunnerQueueRepository.class);
        runnerScheduler = new RunnerScheduler(runnerService,
                containerRepository,
                userRepository,
                projectRunner,
                runnerQueueRepository);

    }

    @Test
    public void whenRunnerIsInQueue_thenRunnerWillBeStarted()
    {
    }

    @Test
    public void whenRunnerIsInQueue_thenAfterStartRunnerWillBeRemovedFromQueue()
    {

    }
}
