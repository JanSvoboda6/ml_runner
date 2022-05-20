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
    public static final String RANDOM_RESULT_TEXT = "Random result text";
    public static final double ACCURACY = 0.99;

    private RunnerController runnerController;
    private RunnerService runnerService;
    private RequestValidator requestValidator;
    private ResultRepository resultRepository;
    private ContainerUtility containerUtility;

    @BeforeEach
    public void before()
    {
        containerUtility = Mockito.mock(ContainerUtility.class);
        requestValidator = Mockito.mock(RequestValidator.class);
        resultRepository = Mockito.mock(ResultRepository.class);
        runnerService = Mockito.mock(RunnerService.class);
        runnerController = new RunnerController(containerUtility, runnerService, requestValidator);
    }


    @Test
    public void whenRequestForResultForRunnerInEndState_thenResultIsReturned() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);

        Result result = Mockito.mock(Result.class);
        Mockito.when(result.getResultText()).thenReturn(RANDOM_RESULT_TEXT);
        Mockito.when(result.getAccuracy()).thenReturn(ACCURACY);

        Mockito.when(runnerService.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID)).thenReturn(Optional.of(result));
        ResponseEntity<?> response = runnerController.getResult(RANDOM_JWT_TOKEN, PROJECT_ID, RUNNER_ID);

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(response.getBody().toString()).contains(RANDOM_RESULT_TEXT);
        Assertions.assertThat(response.getBody().toString()).contains(String.valueOf(ACCURACY));
    }

    @Test
    @Ignore
    public void whenRequestForResultForUnfinishedRunner_thenMessageInReturned() throws JSONException, IOException
    {
        ContainerEntity containerEntity = Mockito.mock(ContainerEntity.class);
        Mockito.when(containerEntity.getId()).thenReturn(CONTAINER_ID);
        Mockito.when(requestValidator.validateContainerEntity(Mockito.anyLong())).thenReturn(containerEntity);

        Mockito.when(runnerService.getResult(CONTAINER_ID, PROJECT_ID, RUNNER_ID)).thenReturn(Optional.empty());
        ResponseEntity<?> response = runnerController.getResult(RANDOM_JWT_TOKEN, PROJECT_ID, RUNNER_ID);

        Assertions.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(response.getBody().toString()).contains("Result cannot be obtained since project is still running!");
    }
}
