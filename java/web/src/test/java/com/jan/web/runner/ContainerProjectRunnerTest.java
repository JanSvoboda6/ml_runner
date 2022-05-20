package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.project.Project;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

public class ContainerProjectRunnerTest
{
    public static final String CONNECTION_STRING = "http://random:9999";
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
        ContainerEntity container = Mockito.mock(ContainerEntity.class);
        Mockito.when(container.getConnectionString()).thenReturn(CONNECTION_STRING);

        projectRunner.run(runner, container);

        Mockito.verify(requestMaker, Mockito.times(1))
                .makePostRequest(Mockito.eq(CONNECTION_STRING), Mockito.eq(RequestMethod.RUN_PROJECT), Mockito.any());
    }
}
