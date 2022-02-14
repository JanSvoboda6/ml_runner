package com.jan.web.runner;

import com.jan.web.Project;
import com.jan.web.RequestMaker;
import com.jan.web.RequestMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class ContainerProjectRunnerTest
{
    public static final int RANDOM_CONTAINER_ID = 999;
    private ContainerProjectRunner projectRunner;
    private RunnerRepository runnerRepository;
    private RequestMaker requestMaker;

    @BeforeEach
    public void before()
    {
        runnerRepository = Mockito.mock(RunnerRepository.class);
        requestMaker = Mockito.mock(RequestMaker.class);
        projectRunner = new ContainerProjectRunner(requestMaker);
    }

    @Test
    public void whenRunMethodIsCalled_thenContainerIsInformed()
    {
        Runner runner = Mockito.mock(Runner.class);
        Project project = Mockito.mock(Project.class);
        Mockito.when(runner.getProject()).thenReturn(project);
        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(runner));

        projectRunner.run(runner, RANDOM_CONTAINER_ID);

        Mockito.verify(requestMaker, Mockito.times(1))
                .makePostRequest(Mockito.anyInt(), Mockito.eq(RequestMethod.RUN_PROJECT), Mockito.any());
    }

    @Test
    public void whenStopMethodIsCalled_thenContainerIsInformed()
    {
        Assertions.fail("Test case not implemented.");
    }
}
