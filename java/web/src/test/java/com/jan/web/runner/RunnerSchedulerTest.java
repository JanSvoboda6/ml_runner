package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.project.Project;
import com.jan.web.runner.scheduling.RunnerQueueEntity;
import com.jan.web.runner.scheduling.RunnerQueueRepository;
import com.jan.web.runner.scheduling.RunnerScheduler;
import com.jan.web.runner.status.RunnerStatus;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RunnerSchedulerTest
{
    private static final long USER_ID = 999L;
    private User user;
    private RunnerScheduler runnerScheduler;
    private RunnerService runnerService;
    private ContainerRepository containerRepository;
    private UserRepository userRepository;
    private ProjectRunner projectRunner;
    private RunnerQueueRepository runnerQueueRepository;

    @BeforeEach
    public void before()
    {
        user = Mockito.mock(User.class);
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
    public void whenRunnerIsInQueueAndNoRunnerIsRunning_thenRunnerWillBeStarted()
    {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Mockito.when(runnerService.isAnyRunnerRunning(user)).thenReturn(false);
        Runner runner = Mockito.mock(Runner.class);
        Project project = Mockito.mock(Project.class);
        Mockito.when(runner.getProject()).thenReturn(project);
        Mockito.when(project.getUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        RunnerQueueEntity runnerQueueEntity = new RunnerQueueEntity(user, runner);
        Mockito.when(runnerQueueRepository.getAllByUser(user)).thenReturn(List.of(runnerQueueEntity));
        ContainerEntity container = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.of(container));

        runnerScheduler.scheduleRunning();

        Mockito.verify(projectRunner, Mockito.times(1)).run(runner, container);
    }


    @Test
    public void whenRunnerIsInQueueAndSomeRunnerIsRunning_thenRunnerWillBeNotStarted()
    {
        runnerScheduler.scheduleRunning();
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Mockito.when(runnerService.isAnyRunnerRunning(user)).thenReturn(true);

        runnerScheduler.scheduleRunning();

        Mockito.verifyZeroInteractions(projectRunner);
    }

    @Test
    public void whenRunnerIsInQueue_thenAfterStartRunnerWillBeRemovedFromQueue()
    {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Mockito.when(runnerService.isAnyRunnerRunning(user)).thenReturn(false);
        Runner runner = Mockito.mock(Runner.class);
        Project project = Mockito.mock(Project.class);
        final long runnerId = 777L;
        Mockito.when(runner.getId()).thenReturn(runnerId);
        Mockito.when(runner.getProject()).thenReturn(project);
        Mockito.when(project.getUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        RunnerQueueEntity runnerQueueEntity = new RunnerQueueEntity(user, runner);
        Mockito.when(runnerQueueRepository.getAllByUser(user)).thenReturn(List.of(runnerQueueEntity));
        ContainerEntity container = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.of(container));

        runnerScheduler.scheduleRunning();

        Mockito.verify(runnerQueueRepository, Mockito.times(1)).deleteByRunnerId(runnerId);
    }


    @Test
    public void whenRunnerIsInQueue_thenAfterStartRunnerStateWillBeChangedToInitial()
    {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Mockito.when(runnerService.isAnyRunnerRunning(user)).thenReturn(false);
        Runner runner = Mockito.mock(Runner.class);
        Project project = Mockito.mock(Project.class);
        final long runnerId = 777L;
        Mockito.when(runner.getId()).thenReturn(runnerId);
        Mockito.when(runner.getProject()).thenReturn(project);
        Mockito.when(project.getUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(USER_ID);
        RunnerQueueEntity runnerQueueEntity = new RunnerQueueEntity(user, runner);
        Mockito.when(runnerQueueRepository.getAllByUser(user)).thenReturn(List.of(runnerQueueEntity));
        ContainerEntity container = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.of(container));

        runnerScheduler.scheduleRunning();

        Mockito.verify(runner, Mockito.times(1)).setStatus(Mockito.eq(RunnerStatus.INITIAL));
    }

    @Test
    public void whenNoRunnerExists_thenNoRunnerWillBeRun()
    {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(runnerService.isAnyRunnerRunning(user)).thenReturn(false);
        Mockito.when(runnerQueueRepository.getAllByUser(user)).thenReturn(Collections.emptyList());
        ContainerEntity container = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findByUserId(Mockito.anyLong())).thenReturn(Optional.of(container));

        runnerScheduler.scheduleRunning();

        Mockito.verifyNoMoreInteractions(projectRunner);
    }
}
