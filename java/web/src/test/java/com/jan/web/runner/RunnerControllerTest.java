package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
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
        Mockito.verify(requestMaker, Mockito.times(1)).makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any());
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForSecondTime_thenResultIsObtainedFromLocalSource() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(999L);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);
        //resultRepository - when result is found then return it
        runnerController.getResult(RANDOM_JWT_TOKEN, 999L, 999L);
        Mockito.verify(requestMaker, Mockito.times(1)).makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any());
        Assertions.fail("Production code not implemented.");
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForFirstTime_thenResultIsPersisted() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(999L);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        runnerController.getResult(RANDOM_JWT_TOKEN,999, 999);
        //Mockito.verify - resultRepository.save was called
        Assertions.fail("Production code not implemented.");
    }

    @Test
    public void whenRequestForResultForUnfinishedRunner_thenMessageInReturned()
    {
        Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRequestForFinished_thenInformationIsObtainedFromContainer()
    {
        FinishedRequest finishedRequest = Mockito.mock(FinishedRequest.class);
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));

        runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verify(requestMaker, Mockito.times(1))
                .makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.IS_RUNNER_FINISHED), Mockito.any());
    }

    @Test
    public void whenRequestForFinishedForAlreadyFinishedRunner_thenInformationIsObtainedFromLocalSource() throws IOException
    {
        FinishedRequest finishedRequest = Mockito.mock(FinishedRequest.class);
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.isFinished()).thenReturn(true);

        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(runner));

        ResponseEntity<?> response = runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verifyZeroInteractions(requestMaker);
        Mockito.verify(runner, Mockito.times(1)).isFinished();
        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.fail("Production code not implemented");
    }

    @Test
    public void whenRunnerIsFinished_thenFinishedInformationIsPersisted() throws IOException
    {
        FinishedRequest finishedRequest = Mockito.mock(FinishedRequest.class);
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        FinishedResponse finishedResponse = Mockito.mock(FinishedResponse.class);
        finishedResponse.isFinished = true;

        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(FinishedResponse.class))).thenReturn(finishedResponse);
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.isFinished()).thenReturn(false);
        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(runner));

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("A random body.");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.IS_RUNNER_FINISHED), Mockito.any()))
                        .thenReturn(responseEntity);

        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any()))
                .thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verify(runner, Mockito.times(1)).setFinished(true);
        Mockito.verify(runnerRepository, Mockito.times(1)).save(runner);
    }

    @Test
    public void whenRunnerIsNotFinished_thenNotFinishedInformationIsPersisted() throws IOException
    {
        FinishedRequest finishedRequest = Mockito.mock(FinishedRequest.class);
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        FinishedResponse finishedResponse = Mockito.mock(FinishedResponse.class);
        finishedResponse.isFinished = false;

        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(FinishedResponse.class))).thenReturn(finishedResponse);
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.isFinished()).thenReturn(false);
        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(runner));

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("A random body.");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.IS_RUNNER_FINISHED), Mockito.any()))
                .thenReturn(responseEntity);

        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any()))
                .thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verify(runner, Mockito.times(0)).setFinished(true);
    }
}
