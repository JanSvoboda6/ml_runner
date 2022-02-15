package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.request.RequestMaker;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.core.Response;
import java.io.IOException;

public class RunnerControllerTest
{
    public static final String RANDOM_JWT_TOKEN = "A random token";
    private RunnerController runnerController;
    private RunnerRepository runnerRepository;
    private ContainerRepository containerRepository;
    private ContainerUtility containerUtility;
    private RunnerService runnerService;
    private RequestValidator requestValidator;
    private RequestMaker requestMaker;
    private ProjectRunner projectRunner;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void before()
    {
        runnerRepository = Mockito.mock(RunnerRepository.class);
        containerRepository = Mockito.mock(ContainerRepository.class);
        containerUtility = Mockito.mock(ContainerUtility.class);
        requestValidator = Mockito.mock(RequestValidator.class);
        requestMaker = Mockito.mock(RequestMaker.class);
        projectRunner = Mockito.mock(ProjectRunner.class);
        runnerService = new RunnerServiceImpl(runnerRepository, projectRunner);
        objectMapper = Mockito.mock(ObjectMapper.class);
        runnerController = new RunnerController(
                runnerRepository,
                containerRepository,
                containerUtility,
                runnerService,
                requestValidator,
                requestMaker,
                objectMapper);
    }

    @Test
    public void whenRequestForRunningProject_thenRunnerIsPersisted()
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(999L);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);

        long projectId = 999;
        RunRequest runRequest = new RunRequest();
        runRequest.setProjectId(projectId);

        runnerController.runProject(RANDOM_JWT_TOKEN, runRequest);
        Mockito.verify(runnerRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForFirstTime_thenResultIsObtainedFromContainer() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(999L);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        runnerController.getResult(RANDOM_JWT_TOKEN, 999L, 999L);
        Mockito.verify(requestMaker, Mockito.times(1)).makePostRequest(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForFirstTime_thenResultIsPersisted()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForSecondTime_thenResultIsObtainedFromLocalSource()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRequestForResultForUnfinishedRunner_thenMessageInReturned()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRequestForFinished_thenInformationIsObtainedFromContainer()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRequestForFinishedForAlreadyFinishedRunner_thenInformationIsObtainedFromLocalSource()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRunnerIsFinished_thenFinishedInformationIsPersisted()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRunnerIsNotFinished_thenNotFinishedInformationIsPersisted()
    {
        Assertions.fail("Test case not implemented");
    }
}
