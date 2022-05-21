package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.project.Project;
import com.jan.web.project.ProjectRepository;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.runner.result.Result;
import com.jan.web.runner.result.ResultRepository;
import com.jan.web.runner.result.ResultResponse;
import com.jan.web.runner.parameter.HyperParameter;
import com.jan.web.runner.parameter.HyperParameterRepository;
import com.jan.web.runner.scheduling.RunnerQueueRepository;
import com.jan.web.runner.status.RunnerStatus;
import com.jan.web.runner.status.StatusResponse;
import org.assertj.core.api.Assertions;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RunnerServiceImplTest
{
    public static final long CONTAINER_ID = 999L;
    public static final long PROJECT_ID = 888L;
    public static final long RUNNER_ID = 777L;

    private RunnerRepository runnerRepository;
    private ContainerRepository containerRepository;
    private RunnerService runnerService;
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
    }

    @Test
    public void whenRequestForRunningProject_thenRunnerIsPersisted()
    {
        runnerService.runProject(List.of(new HyperParameter("gamma", "10")), Mockito.mock(Project.class), Mockito.mock(ContainerEntity.class));
        Mockito.verify(runnerRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForFirstTime_thenResultIsObtainedFromContainer() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(CONTAINER_ID);
        Mockito.when(containerEntity.getConnectionString()).thenReturn("http://random:9999");
        Mockito.when(containerRepository.findById(CONTAINER_ID)).thenReturn(Optional.of(containerEntity));

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        resultResponse.resultText = "Random result text";
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.FINISHED);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        runnerService.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID);
        Mockito.verify(requestMaker, Mockito.times(1)).makePostRequest(Mockito.anyString(), Mockito.eq(RequestMethod.RUNNER_RESULT), Mockito.any());
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForFirstTime_thenResultIsPersisted() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(CONTAINER_ID);
        Mockito.when(containerEntity.getConnectionString()).thenReturn("http://random:9999");
        Mockito.when(containerRepository.findById(CONTAINER_ID)).thenReturn(Optional.of(containerEntity));

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getBody()).thenReturn("");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(responseEntity);

        ResultResponse resultResponse = Mockito.mock(ResultResponse.class);
        resultResponse.resultText = "Random result text";
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ResultResponse.class))).thenReturn(resultResponse);

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.FINISHED);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        runnerService.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID);
        Mockito.verify(resultRepository).save(Mockito.argThat((Result savedResult) -> savedResult.getRunner() == runner));
    }

    @Test
    public void whenRequestForResultForFinishedRunnerForSecondTime_thenResultIsObtainedFromLocalSource() throws JSONException, IOException
    {
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.FINISHED);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        Result result = Mockito.mock(Result.class);
        Mockito.when(resultRepository.findByRunnerId(RUNNER_ID)).thenReturn(Optional.of(result));
        runnerService.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID);
        Mockito.verifyZeroInteractions(requestMaker);
    }

    @Test
    public void whenRequestForResultForResult_thenProperResultIsReturned() throws JSONException, IOException
    {
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.FINISHED);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        Result result = Mockito.mock(Result.class);
        Mockito.when(resultRepository.findByRunnerId(RUNNER_ID)).thenReturn(Optional.of(result));
        Assertions.assertThat(runnerService.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID).get()).isEqualTo(result);
    }

    @Test
    public void whenRequestForRunningRunner_thenNoResultIsReturned() throws JSONException, IOException
    {
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        RunnerService runnerServiceSpy = Mockito.spy(runnerService);
        Mockito.doReturn(RunnerStatus.INITIAL).when(runnerServiceSpy).getStatus(CONTAINER_ID, RUNNER_ID);
        Optional<Result> result = runnerServiceSpy.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID);
        Assertions.assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenRequestForRunnerStatus_thenProperStatusIsReturned() throws JSONException, IOException
    {
        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getId()).thenReturn(RUNNER_ID);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.FINISHED);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        Assertions.assertThat(runnerService.getStatus(CONTAINER_ID, RUNNER_ID)).isEqualTo(RunnerStatus.FINISHED);
    }

    @Test
    public void whenRequestingStatusForRunnerInInitialState_thenInformationIsObtainedFromContainer() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getConnectionString()).thenReturn("http://random:9999");

        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));

        ResponseEntity<String> statusResponseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(statusResponseEntity.getBody()).thenReturn("A random body of status response.");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyString(), Mockito.eq(RequestMethod.RUNNER_STATUS), Mockito.any()))
                .thenReturn(statusResponseEntity);

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getId()).thenReturn(RUNNER_ID);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.INITIAL);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        StatusResponse statusResponse = Mockito.mock(StatusResponse.class);
        statusResponse.chronologicalStatuses = List.of("INITIAL");
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(StatusResponse.class))).thenReturn(statusResponse);

        runnerService.getStatus(CONTAINER_ID, RUNNER_ID);
        Mockito.verify(requestMaker, Mockito.times(1))
                .makePostRequest(Mockito.anyString(), Mockito.eq(RequestMethod.RUNNER_STATUS), Mockito.any());
    }

    @Test
    public void whenRequestingStatusForRunnerInEndState_thenInformationIsObtainedFromLocalSource() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getConnectionString()).thenReturn("http://random:9999");

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getId()).thenReturn(RUNNER_ID);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.FINISHED);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        runnerService.getStatus(CONTAINER_ID, RUNNER_ID);
        Mockito.verifyZeroInteractions(requestMaker);
    }

    @Test
    public void whenRunnerIsSwitchedToEndState_thenInformationIsPersisted() throws IOException, JSONException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getConnectionString()).thenReturn("http://random:9999");

        Mockito.when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerEntity));

        ResponseEntity<String> statusResponseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(statusResponseEntity.getBody()).thenReturn("A random body of status response.");
        Mockito.when(requestMaker.makePostRequest(Mockito.anyString(), Mockito.eq(RequestMethod.RUNNER_STATUS), Mockito.any()))
                .thenReturn(statusResponseEntity);

        Runner runner = Mockito.mock(Runner.class);
        Mockito.when(runner.getId()).thenReturn(RUNNER_ID);
        Mockito.when(runner.getStatus()).thenReturn(RunnerStatus.INITIAL);
        Mockito.when(runnerRepository.findById(RUNNER_ID)).thenReturn(Optional.of(runner));

        StatusResponse statusResponse = Mockito.mock(StatusResponse.class);
        statusResponse.chronologicalStatuses = List.of("INITIAL", "PREPARING_DATA", "TRAINING", "PREDICTING","FINISHED");
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(StatusResponse.class))).thenReturn(statusResponse);

        runnerService.getStatus(CONTAINER_ID, RUNNER_ID);

        Mockito.verify(runner).setStatus(RunnerStatus.FINISHED);
        Mockito.verify(runnerRepository).save(runner);
    }
}
