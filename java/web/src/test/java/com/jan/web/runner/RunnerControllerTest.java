package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.result.Result;
import com.jan.web.result.ResultRepository;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
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
        resultRepository = Mockito.mock(ResultRepository.class);
        runnerController = new RunnerController(
                runnerRepository,
                containerRepository,
                containerUtility,
                runnerService,
                requestValidator,
                requestMaker,
                objectMapper,
                resultRepository);
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

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.isFinished()).thenReturn(true);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        runnerController.getResult(RANDOM_JWT_TOKEN, PROJECT_ID, RUNNER_ID);
        Mockito.verify(requestMaker, Mockito.times(1)).makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any());
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForSecondTime_thenResultIsObtainedFromLocalSource() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(999L);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);
        Result result = Mockito.mock(Result.class);
        Mockito.when(resultRepository.findByRunnerId(Mockito.anyLong())).thenReturn(result);
        ResponseEntity<?> response = runnerController.getResult(RANDOM_JWT_TOKEN, 999L, 999L);
        Mockito.verifyZeroInteractions(requestMaker);
        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForFirstTime_thenResultIsPersisted() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.isFinished()).thenReturn(true);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        runnerController.getResult(RANDOM_JWT_TOKEN, PROJECT_ID, RUNNER_ID);
        Mockito.verify(resultRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void whenRequestForResultForUnfinishedRunner_thenMessageInReturned()
    {
       Assertions.fail("Test case not implemented");
    }

    @Test
    public void whenRequestForFinished_thenInformationIsObtainedFromContainer() throws JSONException, IOException
    {
        FinishedRequest finishedRequest = Mockito.mock(FinishedRequest.class);
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        Mockito.when(containerUtility.getContainerIdFromToken(RANDOM_JWT_TOKEN)).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(CONTAINER_ID)).thenReturn(containerEntity);

        ResponseEntity<String> finishedResponseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(finishedResponseEntity.getBody()).thenReturn("A random body of finished response.");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.IS_RUNNER_FINISHED), Mockito.any()))
                .thenReturn(finishedResponseEntity);

        ResponseEntity<String> resultResponseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(finishedResponseEntity.getBody()).thenReturn("A random body of result response.");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any())).thenReturn(resultResponseEntity);

        FinishedResponse finishedResponse = Mockito.mock(FinishedResponse.class);
        finishedResponse.isFinished = false;
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(FinishedResponse.class))).thenReturn(finishedResponse);

        runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verify(requestMaker, Mockito.times(1))
                .makePostRequest(Mockito.anyLong(), Mockito.eq(RequestMethod.IS_RUNNER_FINISHED), Mockito.any());
    }

    @Test
    public void whenRequestForFinishedForAlreadyFinishedRunner_thenInformationIsObtainedFromLocalSource() throws JSONException, IOException
    {
        FinishedRequest finishedRequest = Mockito.mock(FinishedRequest.class);
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.isFinished()).thenReturn(true);

        Mockito.when(runnerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(runner));

        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        Mockito.when(containerUtility.getContainerIdFromToken(RANDOM_JWT_TOKEN)).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(CONTAINER_ID)).thenReturn(containerEntity);

        ResponseEntity<?> response = runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verifyZeroInteractions(requestMaker);
        Mockito.verify(runner, Mockito.times(1)).isFinished();
        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void whenRunnerIsFinished_thenFinishedInformationIsPersisted() throws IOException, JSONException
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

        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        Mockito.when(containerUtility.getContainerIdFromToken(RANDOM_JWT_TOKEN)).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(CONTAINER_ID)).thenReturn(containerEntity);

        runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verify(runner, Mockito.times(1)).setFinished(true);
        Mockito.verify(runnerRepository, Mockito.times(1)).save(runner);
    }

    @Test
    public void whenRunnerIsNotFinished_thenNotFinishedInformationIsPersisted() throws IOException, JSONException
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

        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));
        Mockito.when(containerUtility.getContainerIdFromToken(RANDOM_JWT_TOKEN)).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(CONTAINER_ID)).thenReturn(containerEntity);

        runnerController.isFinished(RANDOM_JWT_TOKEN, finishedRequest);
        Mockito.verify(runner, Mockito.times(0)).setFinished(true);
    }
}
