package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.project.ProjectRepository;
import com.jan.web.request.RequestMaker;
import com.jan.web.result.Result;
import com.jan.web.result.ResultRepository;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

public class RunnerControllerTest
{
    public static final String RANDOM_JWT_TOKEN = "A random token";
    public static final long CONTAINER_ID = 999L;
    public static final long RUNNER_ID = 999L;
    public static final int PROJECT_ID = 999;
    private RunnerController runnerController;
    private RunnerRepository runnerRepository;
    private ContainerRepository containerRepository;
    private ContainerUtility containerUtility;
    private RunnerService runnerService;
    private RequestValidator requestValidator;
    private RequestMaker requestMaker;
    private ProjectRunner projectRunner;
    private ObjectMapper objectMapper;
    private ResultRepository resultRepository;
    private HyperParameterRepository hyperParameterRepository;
    private ProjectRepository projectRepository;
    private RunnerQueueRepository runnerQueueRepository;

    @BeforeEach
    public void before()
    {
        runnerRepository = Mockito.mock(RunnerRepository.class);
        containerRepository = Mockito.mock(ContainerRepository.class);
        containerUtility = Mockito.mock(ContainerUtility.class);
        requestValidator = Mockito.mock(RequestValidator.class);
        requestMaker = Mockito.mock(RequestMaker.class);
        projectRunner = Mockito.mock(ProjectRunner.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        resultRepository = Mockito.mock(ResultRepository.class);
        hyperParameterRepository = Mockito.mock(HyperParameterRepository.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        runnerQueueRepository = Mockito.mock(RunnerQueueRepository.class);
        runnerService = new RunnerServiceImpl(runnerRepository,
                projectRepository,
                projectRunner,
                containerRepository,
                resultRepository,
                hyperParameterRepository,
                runnerQueueRepository,
                requestMaker,
                objectMapper);
        runnerController = new RunnerController(runnerRepository, containerUtility, runnerService, requestValidator);
    }


    @Test
    public void whenRequestForResultForFinishedRunnerForSecondTime_thenResultIsObtainedFromLocalSource() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);
        Result result = Mockito.mock(Result.class);
        Mockito.when(resultRepository.findByRunnerId(Mockito.anyLong())).thenReturn(Optional.of(result));
        ResponseEntity<?> response = runnerController.getResult(RANDOM_JWT_TOKEN, PROJECT_ID, RUNNER_ID);
        Mockito.verifyZeroInteractions(requestMaker);
        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @Ignore
    public void whenRequestForResultForUnfinishedRunner_thenMessageInReturned()
    {
        //TODO: Jan - implement test case
    }
}
