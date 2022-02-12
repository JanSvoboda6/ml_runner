package com.jan.web.runner;

import com.jan.web.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class ContainerProjectRunnerTest
{
    public static final int RANDOM_CONTAINER_ID = 999;
    private ContainerProjectRunner projectRunner;
    private RestTemplate restTemplate;
    private RunnerRepository runnerRepository;

    @BeforeEach
    public void before()
    {
        restTemplate = Mockito.mock(RestTemplate.class);
        runnerRepository = Mockito.mock(RunnerRepository.class);
        projectRunner = new ContainerProjectRunner(restTemplate, runnerRepository);
    }

    @Test
    public void whenRunMethodIsCalled_thenContainerIsInformed()
    {
        Runner runner = Mockito.mock(Runner.class);
        Project project = Mockito.mock(Project.class);
        Mockito.when(runner.getProject()).thenReturn(project);
        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(runner));

        projectRunner.run(runner, RANDOM_CONTAINER_ID);

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.any(Class.class));
    }

    @Test
    public void whenStopMethodIsCalled_thenContainerIsInformed()
    {
        Assertions.fail("Test case not implemented.");
    }
}
